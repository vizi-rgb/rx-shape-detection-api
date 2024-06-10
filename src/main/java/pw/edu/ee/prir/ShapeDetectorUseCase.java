package pw.edu.ee.prir;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;

public interface ShapeDetectorUseCase {
    Mono<DataBuffer> detect(Mono<DataBuffer> image);
}
