package pw.edu.ee.prir;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DetectionHandler {
    private final ShapeDetectorUseCase shapeDetectorUseCase;

    public Mono<ServerResponse> doDetection(ServerRequest request) {
        final var image = request.multipartData()
            .map(parts -> parts.toSingleValueMap().get("file"))
            .cast(FilePart.class)
            .flatMapMany(FilePart::content)
            .reduce(this::combineDataBuffers)
            .transform(shapeDetectorUseCase::detect);

        return ServerResponse.ok().contentType(MediaType.IMAGE_JPEG).body(BodyInserters.fromDataBuffers(image));
    }

    private DataBuffer combineDataBuffers(DataBuffer db1, DataBuffer db2) {
        byte[] buffer1 = new byte[db1.readableByteCount()];
        byte[] buffer2 = new byte[db2.readableByteCount()];
        db1.read(buffer1);
        db2.read(buffer2);
        byte[] combined = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, combined, 0, buffer1.length);
        System.arraycopy(buffer2, 0, combined, buffer1.length, buffer2.length);

        return DefaultDataBufferFactory.sharedInstance.wrap(combined);
    }
}
