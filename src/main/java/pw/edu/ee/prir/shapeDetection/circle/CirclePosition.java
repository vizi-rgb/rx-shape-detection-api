package pw.edu.ee.prir.shapeDetection.circle;

import org.opencv.core.MatOfPoint;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;

public class CirclePosition implements ShapePosition {

    private final MatOfPoint contour;

    public CirclePosition(MatOfPoint contour) {
        this.contour = contour;
    }

    public MatOfPoint getContour() {
        return contour;
    }
}
