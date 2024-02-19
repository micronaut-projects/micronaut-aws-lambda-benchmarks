plugins {
    id("application") 
    id("java") 
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.micronaut.platform:micronaut-platform:4.3.2"))
    implementation("io.micronaut.starter:micronaut-starter-aws-cdk:4.3.2") {
      exclude(group = "software.amazon.awscdk", module = "aws-cdk-lib")
    }
    implementation("software.amazon.awscdk:aws-cdk-lib:2.110.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}
application {
    mainClass.set("example.micronaut.Main")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

