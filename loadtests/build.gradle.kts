import io.gatling.gradle.LogHttp

plugins {
    java
    id("io.gatling.gradle") version "3.9.5.6"
}
repositories {
    mavenCentral()
}
gatling {
    // WARNING: options below only work when logback config file isn't provided
    logLevel = "WARN" // logback root level
    logHttp = LogHttp.NONE // set to 'ALL' for all HTTP traffic in TRACE, 'FAILURES' for failed HTTP traffic in DEBUG
}