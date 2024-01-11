plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.minimal.application") version "4.1.2"
}

version = "0.1"
group = "com.example"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-events")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.aws:micronaut-aws-apigateway")
    implementation("io.micronaut.aws:micronaut-function-aws")
    runtimeOnly("ch.qos.logback:logback-classic")
}
application {
    mainClass.set("com.example.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

micronaut {
    runtime("lambda_java")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
}



