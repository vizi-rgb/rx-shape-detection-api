package pw.edu.ee.prir.shapeDetection.triangle;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pw.edu.ee.prir.shapeDetection.shared.ContourProcessor;
import pw.edu.ee.prir.shapeDetection.shared.ShapeRecognizer;

import java.util.List;

public class TriangleRecognizer implements ShapeRecognizer<TrianglePosition> {
    @Override
    public List<TrianglePosition> recognizeShapes(Mat image) {
        return findTriangles(image);
    }

    private List<TrianglePosition> findTriangles(Mat image) {
        final var contours = ContourProcessor.extractContoursFromImage(image);

        return contours.stream()
                .filter(contour -> hasThreeCorners(ContourProcessor.approximateContour(contour)))
                .filter(contour -> isTriangleLargeEnough(contour, image.size()))
                .filter(contour -> isTriangleSmallEnough(contour, image.size()))
                .map(TrianglePosition::new)
                .toList();
    }

    private boolean hasThreeCorners(MatOfPoint2f contour) {
        return contour.toArray().length == 3;
    }

    private boolean isTriangleLargeEnough(MatOfPoint contour, Size imageSize) {
        final var minArea = 0.01 * imageSize.width * imageSize.height;

        return Imgproc.contourArea(contour) > minArea;
    }

    private boolean isTriangleSmallEnough(MatOfPoint contour, Size imageSize) {
        final var maxArea = 0.8 * imageSize.width * imageSize.height;

        return Imgproc.contourArea(contour) < maxArea;
    }
}
