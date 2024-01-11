package com.example;

import io.micronaut.function.aws.LambdaApplicationContextBuilder;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiGatewayProxyRequestEventFunctionEnvDeductionTest {


    @Test
    void foo() throws IOException {
        LambdaApplicationContextBuilder lambdaApplicationContextBuilder = new LambdaApplicationContextBuilder();
        assertFalse(lambdaApplicationContextBuilder.isDeduceCloudEnvironment());
    }
}
