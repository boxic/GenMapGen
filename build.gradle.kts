plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

group = "net.boxic"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:b1ad94cd1b")
    implementation("dev.hollowcube:schem:1.2.0")
    implementation("de.articdive:jnoise-pipeline:4.1.0")
}

tasks.test {
    useJUnitPlatform()
}