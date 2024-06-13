package pw.edu.ee.prir.shapeDetection.rectangle;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pw.edu.ee.prir.shapeDetection.shared.ContourProcessor;
import pw.edu.ee.prir.shapeDetection.shared.ShapeRecognizer;

import java.util.List;

public class RectangleRecognizer implements ShapeRecognizer<RectanglePosition> {
    @Override
    public List<RectanglePosition> recognizeShapes(Mat image) {
        return findRectangles(image);
    }

    private List<RectanglePosition> findRectangles(Mat image) {
        final var contours = ContourProcessor.extractContoursFromImage(image);

        return contours.stream()
                .filter(contour -> hasFourCorners(ContourProcessor.approximateContour(contour)))
                .filter(contour -> isRectangleLargeEnough(contour, image.size()))
                .filter(contour -> isRectangleSmallEnough(contour, image.size()))
                .map(RectanglePosition::new)
                .toList();
    }

    private boolean hasFourCorners(MatOfPoint2f contour) {
        return contour.toArray().length == 4;
    }

    private boolean isRectangleLargeEnough(MatOfPoint contour, Size imageSize) {
        final var minArea = 0.01 * imageSize.width * imageSize.height;

        return Imgproc.contourArea(contour) > minArea;
    }

    private boolean isRectangleSmallEnough(MatOfPoint contour, Size imageSize) {
        final var maxArea = 0.8 * imageSize.width * imageSize.height;

        return Imgproc.contourArea(contour) < maxArea;
    }
}
