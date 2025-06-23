plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.deepmedia.tools.deployer") version "0.18.0"
}

group = "io.github.0x1bd"
version = "0.3.0"
description = "Skinport API wrapper written in kotlin"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-encoding:3.1.3")
    implementation("io.ktor:ktor-client-okhttp-jvm:3.1.3")
    implementation("io.ktor:ktor-client-auth:3.1.3")
    implementation("io.ktor:ktor-client-cio-jvm:3.1.3")
    testImplementation(kotlin("test"))

    implementation(libs.bundles.ktor.client)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.datetime)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

java {
    withJavadocJar()
    withSourcesJar()
}

deployer {
    content {
        component {
            fromJava()
        }
    }

    projectInfo {
        description = project.description.toString()
        url = "https://github.com/0x1bd/SkinportKt"
        scm.fromGithub("0x1bd", "SkinportKt")
        license("GNU GPL 3.0", "https://www.gnu.org/licenses/gpl-3.0.txt")
        developer("0x1bd", "0x1bd@proton.me")
        groupId = project.group.toString()
    }

    centralPortalSpec {
        signing.key = secret("SIGNING_KEY")
        signing.password = secret("SIGNING_PASSPHRASE")

        auth.user = secret("CENTRAL_USERNAME")
        auth.password = secret("CENTRAL_PASSWORD")
    }
}