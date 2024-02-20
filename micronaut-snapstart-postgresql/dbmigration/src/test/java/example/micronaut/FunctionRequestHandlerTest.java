package example.micronaut;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionRequestHandlerTest {

    private static FunctionRequestHandler handler;
    private static PostgreSQLContainer<?> postgres;

    @BeforeAll
    public static void setupServer() {
        postgres = new PostgreSQLContainer<>("postgres:15-alpine");
        postgres.start();
        handler = new FunctionRequestHandler() {

            @Override
            protected @NonNull ApplicationContextBuilder newApplicationContextBuilder() {
                ApplicationContextBuilder applicationContextBuilder = super.newApplicationContextBuilder();
                applicationContextBuilder.environments(Environment.TEST);
                applicationContextBuilder.properties(
                Map.of(
                        "micronaut.config-client.enabled", StringUtils.FALSE,
                        "datasources.default.url", postgres.getJdbcUrl(),
                        "datasources.default.username", postgres.getUsername(),
                        "datasources.default.password", postgres.getPassword()));
                return applicationContextBuilder;
            }
        };
    }

    @AfterAll
    public static void stopServer() {
        if (handler != null) {
            handler.getApplicationContext().close();
        }
        if (postgres != null) {
            postgres.stop();
            postgres = null;
        }
    }

    @Test
    public void testHandler() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("GET");
        request.setPath("/");
        APIGatewayProxyResponseEvent response = handler.execute(request);
        assertEquals(200, response.getStatusCode().intValue());
    }
}
