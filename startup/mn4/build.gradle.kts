plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.1"
}

version = "0.1"
group = "mn4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut:micronaut-jackson-databind")

    // Validation
    annotationProcessor("io.micronaut:micronaut-http-validation")

    // Custom runtime
    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")

    // HTTP Client
    implementation("io.micronaut:micronaut-http-client")

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic")
}

application {
    mainClass.set("io.micronaut.function.aws.runtime.MicronautLambdaRuntime")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}


graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("lambda_java")
    testRuntime("junit5")
    nativeLambda {
        lambdaRuntimeClassName.set("io.micronaut.function.aws.runtime.MicronautLambdaRuntime")
    }
    processing {
        incremental(true)
        annotations("mn4.*")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    baseImage.set("amazonlinux:2")
    jdkVersion.set("17")
    args(
        "-XX:MaximumHeapSizePercent=80",
        "-Dio.netty.allocator.numDirectArenas=0",
        "-Dio.netty.noPreferDirect=true"
    )
}



