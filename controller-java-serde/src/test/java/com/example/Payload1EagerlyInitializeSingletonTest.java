package com.example;

import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Payload1EagerlyInitializeSingletonTest {
    //void "verify singletons are eagerly initialized for Payload v1 function"() {
    @Test
    void eagerlyInitialization() throws IOException {
        //given:
        ApiGatewayProxyRequestEventFunction handler = new ApiGatewayProxyRequestEventFunction();

        //expect:
        assertTrue(SingletonBean.PACKAGE.get(ApiGatewayProxyRequestEventFunction.class.getPackageName()));

        //cleanup:
        handler.close();
    }
}
