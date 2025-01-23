plugins {
    kotlin("jvm") version "1.9.22"
}

group = "ch.js.rm2024"
version = "6"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "ch.js.rm2024.MainKt"
    }
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().filter { it.exists() }.map { zipTree(it) }
    })
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}
