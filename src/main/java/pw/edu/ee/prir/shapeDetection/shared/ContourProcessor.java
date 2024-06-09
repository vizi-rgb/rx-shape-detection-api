package pw.edu.ee.prir.shapeDetection.shared;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ContourProcessor {
    public static List<MatOfPoint> extractContoursFromImage(Mat image) {
        final var grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        final var blurredImage = new Mat();
        Imgproc.GaussianBlur(grayImage, blurredImage, new Size(5, 5), 0);

        final var edgesImage = new Mat();
        final int threshold1 = 75;
        final int threshold2 = 10;
        Imgproc.Canny(blurredImage, edgesImage, threshold1, threshold2);

        final List<MatOfPoint> contours = new ArrayList<>();
        final var hierarchy = new Mat();
        Imgproc.findContours(
                edgesImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE
        );

        return contours;
    }

    public static MatOfPoint2f approximateContour(MatOfPoint contour) {
        final var curve = new MatOfPoint2f();
        contour.convertTo(curve, CvType.CV_32FC2);

        final var approxCurve = new MatOfPoint2f();
        final var perimeter = Imgproc.arcLength(curve, true);
        final double epsilon = 0.01 * perimeter;
        final boolean closed = true;
        Imgproc.approxPolyDP(curve, approxCurve, epsilon, closed);

        return approxCurve;
    }
}
