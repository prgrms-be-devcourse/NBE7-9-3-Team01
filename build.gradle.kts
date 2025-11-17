plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.0"

	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "POVI"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

dependencies {

	// Lombok (Java 잔존 코드 대비해서 유지)
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Spring
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// DB
	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")

	// Security / OAuth2 / JWT
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// JSON
	implementation("com.fasterxml.jackson.core:jackson-databind")

	// Mail
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// Thymeleaf
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Kotlin
	implementation(kotlin("stdlib-jdk8"))
	implementation(kotlin("reflect"))

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Mockk — Kotlin 기반 mock library
	testImplementation("io.mockk:mockk:1.13.10")

	// JUnit 5
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

kotlin {
	jvmToolchain(21)
}

tasks.test {
	useJUnitPlatform()
}