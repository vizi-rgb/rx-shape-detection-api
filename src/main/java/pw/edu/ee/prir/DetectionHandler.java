package pw.edu.ee.prir;

import lombok.RequiredArgsConstructor;
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
            .transform(shapeDetectorUseCase::detect);

        return ServerResponse.ok().contentType(MediaType.IMAGE_JPEG).body(BodyInserters.fromDataBuffers(image));
    }
}
