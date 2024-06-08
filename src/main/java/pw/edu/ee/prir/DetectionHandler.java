package pw.edu.ee.prir;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class DetectionHandler {

    public Mono<ServerResponse> doDetection(ServerRequest request) {
        final var image = request.multipartData()
            .map(parts -> parts.toSingleValueMap().get("file"))
            .cast(FilePart.class)
            .flatMapMany(FilePart::content)
            .flatMap(Mono::just);

        return ServerResponse.ok().contentType(MediaType.IMAGE_JPEG).body(BodyInserters.fromDataBuffers(image));
    }

}
