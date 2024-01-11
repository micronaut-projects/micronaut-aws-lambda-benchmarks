package com.example;


import io.micronaut.context.ApplicationContext;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
class SingletonBean {

    static Map<String, Boolean> PACKAGE = new ConcurrentHashMap<>(3);

    SingletonBean(ApplicationContext ctx) {
        ctx.getEnvironment().getPackages().forEach(packageName -> PACKAGE.put(packageName, true));
    }
}
