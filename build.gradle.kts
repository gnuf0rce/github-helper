plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version Versions.mirai
}

group = "io.github.gnuf0rce"
version = "0.1.0"

mirai {
    configureShadow {
        exclude {
            it.path.startsWith("kotlin")
        }
    }
}

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    jcenter()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
//            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
//            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.utils.MiraiExperimentalApi")
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.util.ConsoleExperimentalApi")
        }
    }
}

dependencies {
    implementation(ktor("client-encoding", Versions.ktor)) {
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    implementation(ktor("client-serialization", Versions.ktor)) {
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    // test
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = Versions.junit)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
