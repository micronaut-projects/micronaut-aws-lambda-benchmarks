plugins {
    id("io.micronaut.minimal.application") version "4.1.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
version = "0.1"
group = "com.example"
repositories {
    mavenCentral()
}
dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut.aws:micronaut-aws-apigateway")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.crac:micronaut-crac")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")

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
