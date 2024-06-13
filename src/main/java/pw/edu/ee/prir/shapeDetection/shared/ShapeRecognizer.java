package pw.edu.ee.prir.shapeDetection.shared;

import org.opencv.core.Mat;

import java.util.List;

public interface ShapeRecognizer<T extends ShapePosition> {
    List<T> recognizeShapes(Mat image);
}
