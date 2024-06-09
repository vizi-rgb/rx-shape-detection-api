package pw.edu.ee.prir.shapeDetection.circle;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import pw.edu.ee.prir.shapeDetection.Shape;
import pw.edu.ee.prir.shapeDetection.shared.ShapeDrawer;
import pw.edu.ee.prir.shapeDetection.shared.ShapeDrawerUtils;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;

import java.util.List;

public class CircleDrawer implements ShapeDrawer {
    @Override
    public void drawShapeOutline(Mat image, ShapePosition shapePosition) {
        final var circlePosition = (CirclePosition) shapePosition;
        final int contourIdx = -1;
        final var color = Shape.CIRCLE.color();
        final var thickness = ShapeDrawerUtils.getOutlineThickness(image.size());
        Imgproc.drawContours(
                image, List.of(circlePosition.getContour()), contourIdx, color, thickness
        );

        final var shapeDrawerUtils = new ShapeDrawerUtils(
                image, circlePosition.getContour(), Shape.CIRCLE.color()
        );
        shapeDrawerUtils.putTextOnImage(Shape.CIRCLE.name());
    }
}
