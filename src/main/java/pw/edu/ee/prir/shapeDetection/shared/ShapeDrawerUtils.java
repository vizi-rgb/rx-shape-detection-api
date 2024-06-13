package pw.edu.ee.prir.shapeDetection.shared;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class ShapeDrawerUtils {
    private final Mat image;
    private final MatOfPoint contour;
    private final Scalar fontColor;

    public ShapeDrawerUtils(Mat image, MatOfPoint contour, Scalar fontColor) {
        this.image = image;
        this.contour = contour;
        this.fontColor = fontColor;
    }

    public static int getOutlineThickness(Size size) {
        return (int) Math.min(size.width, size.height) / 100;
    }

    public void putTextOnImage(String text) {
        final var topLeftPoint = findTopLeftPoint(contour);
        final int offset = getTextOffset(image.size());
        final var textStartPoint = movePointUp(topLeftPoint, offset);
        final int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        final double fontScale = getFontScale(image.size());
        final int thickness = 2;
        Imgproc.putText(image, text, textStartPoint, fontFace, fontScale, this.fontColor, thickness);

    }

    private Point findTopLeftPoint(MatOfPoint contour) {
        final var boundingRectangle = Imgproc.boundingRect(contour);
        return new Point(boundingRectangle.x, boundingRectangle.y);
    }

    private int getTextOffset(Size imageSize) {
        return (int) Math.min(imageSize.width, imageSize.height) / 50;
    }

    private double getFontScale(Size imageSize) {
        return Math.min(imageSize.width, imageSize.height) * 10e-4;
    }

    private Point movePointUp(Point point, int distance) {
        return new Point(point.x, point.y - distance);
    }
}
