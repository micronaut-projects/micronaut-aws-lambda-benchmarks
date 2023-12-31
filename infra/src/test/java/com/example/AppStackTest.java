package com.example;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import java.io.File;
import java.util.Collections;

class AppStackTest {
    @Test
    void testAppStack() {
        if (
                new File(AppStack.functionPath("controller-java-jacksondatabind", false)).exists() &&
                        new File(AppStack.functionPath("controller-java-snapstart-jacksondatabind", false)).exists() &&
                        new File(AppStack.functionPath("handler-java-snapstart-jacksondatabind", false)).exists()
        ) {
            AppStack stack = new AppStack(new App(), "MnAwsLambdaBenchmark");
            Template template = Template.fromStack(stack);
            //template.hasResourceProperties("AWS::Lambda::Function", Collections.singletonMap("Handler", "io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction"));
        }
    }
}
