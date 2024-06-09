package pw.edu.ee.prir.shapeDetection.shared;

import org.opencv.core.Mat;

public interface ShapeDrawer {
    void drawShapeOutline(Mat image, ShapePosition shapePosition);
}
