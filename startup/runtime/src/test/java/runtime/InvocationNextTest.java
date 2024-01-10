package runtime;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.http.*;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class InvocationNextTest {
    private static final String LAMBDA_RUNTIME_AWS_REQUEST_ID = "Lambda-Runtime-Aws-Request-Id";

    @Test
    void testNextInvocation(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();

        // non existing response
        HttpResponse<?> noContentResponse = assertDoesNotThrow(() ->
                client.exchange(HttpRequest.GET(UriBuilder.of("/response").path("123456").build())));
        assertEquals(HttpStatus.NO_CONTENT, noContentResponse.getStatus());

        // fetch the next invocation
        HttpResponse<APIGatewayProxyRequestEvent> response = assertDoesNotThrow(() ->
                client.exchange(HttpRequest.GET(invocationBuilder().path("next").build()), APIGatewayProxyRequestEvent.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        APIGatewayProxyRequestEvent requestEvent = response.getBody().get();
        assertEquals("/", requestEvent.getPath());
        assertEquals(HttpMethod.GET.toString(), requestEvent.getHttpMethod());
        assertEquals(MediaType.APPLICATION_JSON, requestEvent.getHeaders().get(HttpHeaders.ACCEPT));
        String requestId = response.getHeaders().get(LAMBDA_RUNTIME_AWS_REQUEST_ID);
        assertNotNull(requestId);

        // send a response

        URI invocationResponseUri = invocationBuilder().path(requestId).path("response").build();
        APIGatewayProxyResponseEvent proxyResponseEvent = new APIGatewayProxyResponseEvent();
        proxyResponseEvent.setStatusCode(HttpStatus.OK.getCode());
        String expectedBody = "{\"message\":\"Hello World\"}";
        proxyResponseEvent.setBody(expectedBody);
        HttpResponse<?> invocationResponse = assertDoesNotThrow(() ->
                client.exchange(HttpRequest.POST(invocationResponseUri, proxyResponseEvent)));
        assertEquals(HttpStatus.ACCEPTED, invocationResponse.getStatus());

        HttpResponse<String> helloWorldResponse = assertDoesNotThrow(() ->
                client.exchange(HttpRequest.GET(UriBuilder.of("/response").path("123456").build()), String.class));
        assertEquals(HttpStatus.OK, helloWorldResponse.getStatus());

        assertTrue(helloWorldResponse.getBody().isPresent());
        assertEquals(expectedBody, helloWorldResponse.getBody().get());
    }

    private static UriBuilder invocationBuilder() {
        return UriBuilder.of("/2018-06-01").path("runtime").path("invocation");
    }
}
