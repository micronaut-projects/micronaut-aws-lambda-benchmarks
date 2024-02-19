package example.micronaut;
import io.micronaut.function.aws.MicronautRequestHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import io.micronaut.json.JsonMapper;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Collections;
public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    JsonMapper jsonMapper;

    @Inject
    MessageHandler messageHandler;

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request) {
        if (request.getHttpMethod().equals("POST")) {
            try {
                messageHandler.saveMessage(request);
            } catch (IOException e) {
                return APIGatewayProxyResponseEventUtils.serverError();
            }
        }
        return APIGatewayProxyResponseEventUtils.responseWith(jsonMapper, Collections.singletonMap("message", "Hello Moon"));
    }
}
