package pw.edu.ee.prir;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

public interface ShapeDetector {
    Flux<DataBuffer> detect(Flux<DataBuffer> image);
}
