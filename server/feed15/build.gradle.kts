import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
        id("org.springframework.boot") version "3.2.0"
        id("io.spring.dependency-management") version "1.1.4"
        kotlin("jvm") version "1.9.20"
        kotlin("plugin.spring") version "1.9.20"
}

group = "info.glennengstrand"
version = "0.0.1-SNAPSHOT"

java {
        sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
        mavenCentral()
}

dependencies {
        // implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
        // implementation("io.asyncer:r2dbc-mysql:1.0.4")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
        kotlinOptions {
                freeCompilerArgs += "-Xjsr305=strict"
                jvmTarget = "17"
        }
}

tasks.withType<Test> {
        useJUnitPlatform()
}
