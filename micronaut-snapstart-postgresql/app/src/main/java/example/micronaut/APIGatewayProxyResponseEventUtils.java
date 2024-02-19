package example.micronaut;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

public final class APIGatewayProxyResponseEventUtils {

    private APIGatewayProxyResponseEventUtils() {

    }

    public static APIGatewayProxyResponseEvent responseWith(JsonMapper jsonMapper, Map<String, Object> body) {
        try {
            String json = new String(jsonMapper.writeValueAsBytes(body));
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody(json);
            return response;
        } catch (IOException e) {
            return serverError();
        }
    }

    public static APIGatewayProxyResponseEvent serverError() {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(500);
        return response;
    }
}
