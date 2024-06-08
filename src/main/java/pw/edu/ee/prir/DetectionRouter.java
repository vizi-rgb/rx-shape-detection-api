package pw.edu.ee.prir;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class DetectionRouter {

    private static final String DETECTION_PATH = "/detection";

    @Bean
    public RouterFunction<ServerResponse> route(DetectionHandler detectionHandler) {

        return RouterFunctions
            .route(
                POST("%s/upload".formatted(DETECTION_PATH))
                    .and(accept(MediaType.MULTIPART_FORM_DATA)),
                detectionHandler::doDetection
            );
    }

}
