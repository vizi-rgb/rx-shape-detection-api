package pw.edu.ee.prir;

import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import pw.edu.ee.prir.shapeDetection.Shape;
import pw.edu.ee.prir.shapeDetection.circle.CircleDrawer;
import pw.edu.ee.prir.shapeDetection.circle.CircleRecognizer;
import pw.edu.ee.prir.shapeDetection.rectangle.RectangleDrawer;
import pw.edu.ee.prir.shapeDetection.rectangle.RectangleRecognizer;
import pw.edu.ee.prir.shapeDetection.shared.ShapeOperations;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;
import pw.edu.ee.prir.shapeDetection.triangle.TriangleDrawer;
import pw.edu.ee.prir.shapeDetection.triangle.TriangleRecognizer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShapeDetectorService implements ShapeDetectorUseCase {
    @Override
    public Flux<DataBuffer> detect(Flux<DataBuffer> image) {
        final var matImage = convertToMat(image);
        final var matImageWithShapes = recognizeShapes(matImage);
        return convertMatToDataBuffer(matImageWithShapes);
    }

    private Mat convertToMat(Flux<DataBuffer> image) {
        byte[] imageBytes = image.flatMap(dataBuffer -> Flux.just(dataBuffer.asByteBuffer().array())).blockFirst();
        return Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
    }

    private Mat recognizeShapes(Mat sourceImage) {
        final Map<Shape, ShapeOperations<? extends ShapePosition>> shapeOperations = Map.of(
                Shape.CIRCLE, new ShapeOperations<>(new CircleRecognizer(), new CircleDrawer()),
                Shape.RECTANGLE, new ShapeOperations<>(new RectangleRecognizer(), new RectangleDrawer()),
                Shape.TRIANGLE, new ShapeOperations<>(new TriangleRecognizer(), new TriangleDrawer())
        );

        final var outputImage = sourceImage.clone();

        Map<Shape, List<ShapePosition>> recognizedShapes = shapeOperations.entrySet().stream()
                .parallel()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().shapeRecognizer().recognizeShapes(sourceImage).stream()
                                .map(shapePosition -> (ShapePosition) shapePosition)
                                .collect(Collectors.toList())
                ));

        recognizedShapes.forEach((shape, shapePositions) -> {
            final var operations = shapeOperations.get(shape);
            shapePositions.forEach(recognizedShape ->
                    operations.shapeDrawer().drawShapeOutline(outputImage, recognizedShape)
            );
        });

        return outputImage;
    }

    private Flux<DataBuffer> convertMatToDataBuffer(Mat mat) {
        return Mono.fromCallable(() -> {
                    byte[] matBytes = new byte[(int) (mat.total() * mat.elemSize())];
                    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(matBytes));

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", baos);
                    byte[] bytes = baos.toByteArray();

                    final var dataBuffer = new DefaultDataBufferFactory().allocateBuffer(bytes.length);
                    dataBuffer.write(bytes);
                    return (DataBuffer)dataBuffer;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flux();
    }
}
