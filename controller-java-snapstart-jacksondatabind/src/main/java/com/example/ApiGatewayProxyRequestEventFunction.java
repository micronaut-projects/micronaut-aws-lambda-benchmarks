package com.example;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.function.aws.LambdaApplicationContextBuilder;

public class ApiGatewayProxyRequestEventFunction extends io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction {

    @NonNull
    @Override
    protected ApplicationContextBuilder newApplicationContextBuilder() {
        return new LambdaApplicationContextBuilder();
    }
}
