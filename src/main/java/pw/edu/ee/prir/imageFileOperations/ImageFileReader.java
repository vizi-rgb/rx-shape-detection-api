package pw.edu.ee.prir.imageFileOperations;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ImageFileReader {
    public List<ImageInfo> loadImages(String catalogPath) {
        final var parentCatalogPath = Paths.get(catalogPath);
        final var imageFilesStream = Stream.of(
                Objects.requireNonNull(parentCatalogPath.toFile().listFiles())
        );

        return imageFilesStream
                .map(File::getPath)
                .map(path -> {
                    final var name = path.substring(path.lastIndexOf(File.separator) + 1);
                    return new ImageInfo(loadImage(path), name);
                })
                .toList();
    }

    public Mat loadImage(String path) {
        final BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(),image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);

        return mat;
    }
}
