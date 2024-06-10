package pw.edu.ee.prir;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import pw.edu.ee.prir.shapeDetection.Shape;
import pw.edu.ee.prir.shapeDetection.circle.CircleDrawer;
import pw.edu.ee.prir.shapeDetection.circle.CircleRecognizer;
import pw.edu.ee.prir.shapeDetection.rectangle.RectangleDrawer;
import pw.edu.ee.prir.shapeDetection.rectangle.RectangleRecognizer;
import pw.edu.ee.prir.shapeDetection.shared.ShapeOperations;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;
import pw.edu.ee.prir.shapeDetection.triangle.TriangleDrawer;
import pw.edu.ee.prir.shapeDetection.triangle.TriangleRecognizer;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShapeDetectorService implements ShapeDetectorUseCase {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    @SneakyThrows
    @Override
    public Mono<DataBuffer> detect(Mono<DataBuffer> image) {
        return image
            .map(this::convertToMat)
            .map(this::recognizeShapes)
            .flatMap(this::convertMatToDataBuffer);
    }

    private Mat convertToMat(DataBuffer image) {
        if (image == null) {
            throw new IllegalArgumentException("DataBuffer is null");
        }

        try (final var data = image.asInputStream()) {
            byte[] imageData = data.readAllBytes();
            if (imageData.length == 0) {
                throw new IllegalArgumentException("Empty image data");
            }

            Mat mat = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_COLOR);
            if (mat.empty()) {
                throw new IllegalArgumentException("Failed to decode image");
            }
            return mat;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private Mono<DataBuffer> convertMatToDataBuffer(Mat mat) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, mob);
        byte[] byteArray = mob.toArray();
        DataBuffer buffer = DefaultDataBufferFactory.sharedInstance.wrap(byteArray);
        return Mono.just(buffer);
    }
}
