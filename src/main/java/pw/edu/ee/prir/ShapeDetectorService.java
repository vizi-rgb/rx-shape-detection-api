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
import reactor.core.publisher.Flux;

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
    public Flux<DataBuffer> detect(Flux<DataBuffer> image) {
        return image.take(1)
            .map(this::convertToMat)
            .map(this::recognizeShapes)
            .flatMap(this::convertMatToDataBuffer);
    }

    private Mat convertToMat(DataBuffer image) {
        try (final var data = image.asInputStream()) {
            return Imgcodecs.imdecode(new MatOfByte(data.readAllBytes()), Imgcodecs.IMREAD_COLOR);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read image", e);
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

    private Flux<DataBuffer> convertMatToDataBuffer(Mat mat) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, mob);
        byte[] byteArray = mob.toArray();
        DataBuffer buffer = DefaultDataBufferFactory.sharedInstance.wrap(byteArray);
        return Flux.just(buffer);
    }
}
