package runtime;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
class RuntimeController {
    private static final Logger LOG = LoggerFactory.getLogger(RuntimeController.class);
    private static final String LAMBDA_RUNTIME_AWS_REQUEST_ID = "Lambda-Runtime-Aws-Request-Id";
    private static final String AWS_REQUEST_ID = "123456";
    public static final String PATH = "/";

    private final APIGatewayProxyRequestEvent event;

    private AtomicBoolean eventInvoked = new AtomicBoolean(false);

    private Map<String, APIGatewayProxyResponseEvent> responses = new ConcurrentHashMap<>();
    RuntimeController() {
        this.event = new APIGatewayProxyRequestEvent();
        event.setHttpMethod(HttpMethod.GET.toString());
        event.setPath(PATH);
        event.setHeaders(Collections.singletonMap(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON));
    }

    @Get("/2018-06-01/runtime/invocation/next")
    HttpResponse<?> nextInvocation() {
        if (!eventInvoked.get()) {
            eventInvoked.set(true);
            return HttpResponse.ok(event)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(LAMBDA_RUNTIME_AWS_REQUEST_ID, AWS_REQUEST_ID);
        }
        return HttpResponse.noContent();
    }

    @Post("/2018-06-01/runtime/invocation/{requestId}/response")
    HttpResponse<?> response(@PathVariable("requestId") String requestId, @Body APIGatewayProxyResponseEvent proxyResponse) {
        responses.computeIfAbsent(requestId, it -> proxyResponse);
        return HttpResponse.accepted();
    }

    @Get("/response/{requestId}")
    HttpResponse<?> responseBody(@PathVariable("requestId") String requestId) {
        if (!responses.containsKey(requestId)) {
            return HttpResponse.noContent();
        }
        return HttpResponse.ok(responses.get(requestId).getBody());
    }

    @Delete("/response")
    HttpResponse<?> delete() {
        responses.clear();
        eventInvoked.set(false);
        return HttpResponse.accepted();
    }


}
