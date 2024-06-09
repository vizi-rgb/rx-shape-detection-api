package pw.edu.ee.prir.shapeDetection.circle;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pw.edu.ee.prir.shapeDetection.shared.ContourProcessor;
import pw.edu.ee.prir.shapeDetection.shared.ShapeRecognizer;

import java.util.List;

public class CircleRecognizer implements ShapeRecognizer<CirclePosition> {
    @Override
    public List<CirclePosition> recognizeShapes(Mat image) {
        return findCircles(image);
    }

    private List<CirclePosition> findCircles(Mat image) {
        final var contours = ContourProcessor.extractContoursFromImage(image);

        return contours.stream()
                .filter(contour -> hasAcceptableCircularity(ContourProcessor.approximateContour(contour)))
                .filter(contour -> isCircleLargeEnough(contour, image.size()))
                .filter(contour -> isCircleSmallEnough(contour, image.size()))
                .map(CirclePosition::new)
                .toList();
    }

    private boolean hasAcceptableCircularity(MatOfPoint2f contour) {
        final var perimeter = Imgproc.arcLength(contour, true);
        double circularity = 0.0;
        if (perimeter != 0) {
            circularity = 4 * Math.PI * Imgproc.contourArea(contour) / Math.pow(perimeter, 2);
        }

        return circularity > 0.91;
    }

    private boolean isCircleLargeEnough(MatOfPoint contour, Size imageSize) {
        final var minArea = 0.0005 * imageSize.width * imageSize.height;
        final var circleArea = Imgproc.contourArea(contour);

        return circleArea > minArea;
    }

    private boolean isCircleSmallEnough(MatOfPoint contour, Size imageSize) {
        final var maxArea = 0.8 * imageSize.width * imageSize.height;
        final var circleArea = Imgproc.contourArea(contour);

        return circleArea < maxArea;
    }
}
