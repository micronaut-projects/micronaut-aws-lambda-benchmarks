plugins {
    id("io.micronaut.library") version "4.3.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

version = "0.1"
group = "example.micronaut"

repositories {
    mavenCentral()
}

dependencies {
    // Liquibase
    implementation("io.micronaut.liquibase:micronaut-liquibase")

    // Micronaut Data
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("org.postgresql:postgresql")

    // Micronaut Serialization
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")

    // Micronaut AWS
    implementation("io.micronaut.aws:micronaut-function-aws")

    // Logback
    runtimeOnly("ch.qos.logback:logback-classic")

    // Micronaut HTTP Client
    implementation("io.micronaut:micronaut-http-client-jdk")

    // Secrets Manager
    implementation("io.micronaut.aws:micronaut-aws-secretsmanager")

    // Testcontainers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
}


java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}


micronaut {
    runtime("lambda_java")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("example.micronaut.*")
    }
}



