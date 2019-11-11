plugins {
    application
}

group = "com.rnbymike"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClassName = "App"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    compile("io.javalin", "javalin", "3.6.0")
    compile("com.fasterxml.jackson.core", "jackson-databind", "2.10.0")
    compile("org.slf4j", "slf4j-simple", "1.7.29")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.5.2")
}