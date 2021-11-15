plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version Versions.mirai
    id("net.mamoe.maven-central-publish") version "0.6.1"
}

group = "io.github.gnuf0rce"
version = "1.1.4"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("gnuf0rce", "github-helper", "cssxsh")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

mirai {
    configureShadow {
        listOf(
            "kotlin",
            "org/intellij",
            "org/jetbrains",
            "org/slf4j"
        ).forEach { prefix ->
            exclude { element ->
                element.path.startsWith(prefix)
            }
        }
    }
}

repositories {
    clear()
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
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
    implementation(ktor("client-encoding", Versions.ktor)) {
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    implementation(ktor("client-serialization", Versions.ktor)) {
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    // test
    testImplementation("com.vladsch.flexmark:flexmark-all:0.62.2")
    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation("org.apache.xmlgraphics:batik-transcoder:1.14")
    testImplementation(kotlin("test", Versions.kotlin))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
