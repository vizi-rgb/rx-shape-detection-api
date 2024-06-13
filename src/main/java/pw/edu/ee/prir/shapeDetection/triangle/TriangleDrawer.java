package pw.edu.ee.prir.shapeDetection.triangle;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import pw.edu.ee.prir.shapeDetection.Shape;
import pw.edu.ee.prir.shapeDetection.shared.ShapeDrawer;
import pw.edu.ee.prir.shapeDetection.shared.ShapeDrawerUtils;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;

import java.util.List;

public class TriangleDrawer implements ShapeDrawer {
    @Override
    public void drawShapeOutline(Mat image, ShapePosition shapePosition) {
        final var trianglePosition = (TrianglePosition) shapePosition;
        final int contourIdx = -1;
        final var color = Shape.TRIANGLE.color();
        final var thickness = ShapeDrawerUtils.getOutlineThickness(image.size());
        Imgproc.drawContours(
                image, List.of(trianglePosition.getContour()), contourIdx, color, thickness
        );

        final var shapeDrawerUtils = new ShapeDrawerUtils(
                image, trianglePosition.getContour(), Shape.TRIANGLE.color()
        );
        shapeDrawerUtils.putTextOnImage(Shape.TRIANGLE.name());
    }
}
