plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"

    id("net.mamoe.mirai-console") version "2.14.0-RC"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "io.github.gnuf0rce"
version = "1.2.10"

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
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-selenium-plugin:2.2.4")
    compileOnly("xyz.cssxsh.mirai:mirai-administrator:1.3.0")
    testImplementation(kotlin("test"))
    testImplementation("xyz.cssxsh.mirai:mirai-selenium-plugin:2.2.4")
    //
    implementation(platform("net.mamoe:mirai-bom:2.14.0-RC"))
    compileOnly("net.mamoe:mirai-console-compiler-common")
    testImplementation("net.mamoe:mirai-core-mock")
    //
    implementation(platform("io.ktor:ktor-bom:2.1.3"))
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-encoding")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    //
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps")
    //
    implementation(platform("org.slf4j:slf4j-parent:2.0.6"))
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
