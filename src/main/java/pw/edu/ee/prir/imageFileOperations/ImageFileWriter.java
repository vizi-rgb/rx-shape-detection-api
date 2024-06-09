package pw.edu.ee.prir.imageFileOperations;

import org.opencv.imgcodecs.Imgcodecs;

import java.nio.file.Path;

public class ImageFileWriter {

    public void saveImage(ImageInfo imageInfo, Path parentCatalogPath) {
        final var path = parentCatalogPath.resolve(imageInfo.fileName());
        Imgcodecs.imwrite(path.toString(), imageInfo.image());
    }
}
