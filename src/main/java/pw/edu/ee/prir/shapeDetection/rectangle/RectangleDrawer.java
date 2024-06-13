package pw.edu.ee.prir.shapeDetection.rectangle;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import pw.edu.ee.prir.shapeDetection.Shape;
import pw.edu.ee.prir.shapeDetection.shared.ShapeDrawer;
import pw.edu.ee.prir.shapeDetection.shared.ShapeDrawerUtils;
import pw.edu.ee.prir.shapeDetection.shared.ShapePosition;

import java.util.List;

public class RectangleDrawer implements ShapeDrawer {
    @Override
    public void drawShapeOutline(Mat image, ShapePosition shapePosition) {
        final var rectanglePosition = (RectanglePosition) shapePosition;
        final int contourIdx = -1;
        final var color = Shape.RECTANGLE.color();
        final var thickness = ShapeDrawerUtils.getOutlineThickness(image.size());
        Imgproc.drawContours(
                image, List.of(rectanglePosition.getContour()), contourIdx, color, thickness
        );

        final var shapeDrawerUtils = new ShapeDrawerUtils(
                image, rectanglePosition.getContour(), Shape.RECTANGLE.color()
        );
        shapeDrawerUtils.putTextOnImage(Shape.RECTANGLE.name());
    }
}
