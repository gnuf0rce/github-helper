plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("net.mamoe.mirai-console") version "2.10.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "io.github.gnuf0rce"
version = "1.2.0"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("gnuf0rce", "github-helper", "cssxsh")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
    configureShadow {
        exclude("module-info.class")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-client-serialization:1.6.5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
        exclude(group = "io.ktor", module = "ktor-client-core-jvm")
    }
    implementation("io.ktor:ktor-client-encoding:1.6.5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
        exclude(group = "io.ktor", module = "ktor-client-core-jvm")
    }
    compileOnly("net.mamoe:mirai-core-utils:2.10.0")
    compileOnly("xyz.cssxsh.mirai:mirai-selenium-plugin:2.0.7")
    compileOnly("xyz.cssxsh.mirai:mirai-administrator:1.0.0-RC3")
    // test
    testImplementation("com.vladsch.flexmark:flexmark-all:0.62.2")
    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation("org.apache.xmlgraphics:batik-transcoder:1.14")
    testImplementation(kotlin("test", "1.6.0"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
