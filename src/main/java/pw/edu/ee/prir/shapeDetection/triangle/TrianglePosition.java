package pw.edu.ee.prir.shapeDetection.triangle;

import org.opencv.core.MatOfPoint;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;

public class TrianglePosition implements ShapePosition {
    private final MatOfPoint contour;

    public TrianglePosition(MatOfPoint contour) {
        this.contour = contour;
    }

    public MatOfPoint getContour() {
        return contour;
    }
}
