plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.10"
}

version = "0.1"
group = "mn3"

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
    processing {
        incremental(true)
        annotations("mn3.*")
    }
}



