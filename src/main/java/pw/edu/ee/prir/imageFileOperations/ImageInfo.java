package pw.edu.ee.prir.imageFileOperations;

import org.opencv.core.Mat;

public record ImageInfo(
    Mat image,
    String fileName
) {
}
