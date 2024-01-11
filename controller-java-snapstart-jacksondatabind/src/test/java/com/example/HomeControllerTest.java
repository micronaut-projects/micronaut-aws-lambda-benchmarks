package com.example;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {
    private static MicronautLambdaHandler handler;

    @BeforeAll
    static void setupSpec() throws ContainerInitializationException {
        handler = new MicronautLambdaHandler();
    }
    @AfterAll
    static void cleanupSpec() {
        handler.getApplicationContext().close();
    }

    @Test
    void testHandler() {
        AwsProxyRequest request = new AwsProxyRequest();
        request.setPath("/");
        request.setHttpMethod(HttpMethod.GET.toString());
        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals("{\"message\":\"Hello World\"}",  response.getBody());
    }
}
