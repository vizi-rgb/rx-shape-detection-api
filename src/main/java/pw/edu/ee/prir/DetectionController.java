package pw.edu.ee.prir;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/detection")
public class DetectionController {

    @PostMapping("/upload")
    public Mono<String> uploadFile() {
        return Mono.just("File uploaded successfully")
                .delayElement(Duration.ofSeconds(10));
    }

}
