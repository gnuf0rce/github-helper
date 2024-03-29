plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"

    id("net.mamoe.mirai-console") version "2.15.0"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "io.github.gnuf0rce"
version = "1.4.1"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("gnuf0rce", "github-helper", "cssxsh")
    licenseFromGitHubProject("AGPL-3.0")
    workingDir = System.getenv("PUBLICATION_TEMP")?.let { file(it).resolve(projectName) }
        ?: buildDir.resolve("publishing-tmp")
    publication {
        artifact(tasks["buildPlugin"])
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-selenium-plugin:2.3.0")
    compileOnly("xyz.cssxsh.mirai:mirai-administrator:1.4.0")
    testImplementation(kotlin("test"))
    testImplementation("xyz.cssxsh.mirai:mirai-selenium-plugin:2.3.0")
    //
    implementation(platform("net.mamoe:mirai-bom:2.15.0"))
    compileOnly("net.mamoe:mirai-console-compiler-common")
    testImplementation("net.mamoe:mirai-core-mock")
    testImplementation("net.mamoe:mirai-logging-slf4j")
    //
    implementation(platform("io.ktor:ktor-bom:2.3.3"))
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-encoding")
    implementation("io.ktor:ktor-client-auth")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    //
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps")
    //
    implementation(platform("org.slf4j:slf4j-parent:2.0.7"))
    testImplementation("org.slf4j:slf4j-simple")
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}
