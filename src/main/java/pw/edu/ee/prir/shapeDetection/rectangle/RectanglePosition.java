package pw.edu.ee.prir.shapeDetection.rectangle;

import org.opencv.core.MatOfPoint;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;

public class RectanglePosition implements ShapePosition {
    private final MatOfPoint contour;

    public RectanglePosition(MatOfPoint contour) {
        this.contour = contour;
    }

    public MatOfPoint getContour() {
        return contour;
    }
}
