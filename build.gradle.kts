plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.deepmedia.tools.deployer") version "0.18.0"
}

group = "io.github.0x1bd"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-brotli:4.12.0")

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