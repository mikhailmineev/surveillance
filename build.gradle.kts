import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("io.freefair.lombok") version "6.1.0-m3"
    id("org.jetbrains.kotlin.jvm") version "1.5.30"
    kotlin("plugin.jpa") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
}

group = "org.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    compileOnly("org.projectlombok:lombok:1.18.20")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
}

sourceSets {
    main {
        resources {
            srcDirs("build/resources/bin")
        }
    }
}

tasks.withType<ProcessResources> {
    ant.withGroovyBuilder {
        "mkdir"("dir" to "build/ffmpeg/win")
        "get"("src" to "https://github.com/BtbN/FFmpeg-Builds/releases/download/${project.extra["ffmpegBuildTag"]}/${project.extra["ffmpegVersion"]}.zip", "dest" to "build/ffmpeg/win/ffmpeg.zip", "skipexisting" to "true")
        "unzip"("src" to "build/ffmpeg/win/ffmpeg.zip", "dest" to "build/ffmpeg/win/")
        "copy"("tofile" to "build/resources/bin/ffmpeg/win/ffmpeg.exe") {
            "fileset"("file" to "build/ffmpeg/win/${project.extra["ffmpegVersion"]}/bin/ffmpeg.exe")
        }
    }

    ant.withGroovyBuilder {
        "mkdir"("dir" to "build/ffmpeg/mac")
        "get"("src" to "https://evermeet.cx/ffmpeg/getrelease/zip", "dest" to "build/ffmpeg/mac/ffmpeg.zip", "skipexisting" to "true")
        "unzip"("src" to "build/ffmpeg/mac/ffmpeg.zip", "dest" to "build/ffmpeg/mac/")
        "copy"("tofile" to "build/resources/bin/ffmpeg/mac/ffmpeg") {
            "fileset"("file" to "build/ffmpeg/mac/ffmpeg")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("surveillance.jar")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
