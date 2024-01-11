package com.example;

import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Payload1EagerlyInitializeSingletonTest {
    //void "verify singletons are eagerly initialized for Payload v1 function"() {
    @Disabled
    @Test
    void eagerlyInitialization() throws Exception {
        //given:
        MicronautLambdaHandler handler = new MicronautLambdaHandler();

        //expect:
        assertNotNull(SingletonBean.PACKAGE.get(HomeController.class.getPackageName()));
        assertTrue(SingletonBean.PACKAGE.get(HomeController.class.getPackageName()));

        //cleanup:
        handler.close();
    }
}
