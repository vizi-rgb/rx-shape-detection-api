package pw.edu.ee.prir.shapeDetection.shared;

public record ShapeOperations<T extends ShapePosition>(
        ShapeRecognizer<T> shapeRecognizer,
        ShapeDrawer shapeDrawer
) {
}
