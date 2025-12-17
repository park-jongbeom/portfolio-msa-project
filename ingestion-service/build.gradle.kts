val springCloudVersion = "2025.0.0"

plugins {
    // 루트에서 버전을 관리하므로 여기서는 버전 없이 ID만 적습니다.
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") version "1.9.22"
}

group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "ingestion-service"

dependencies {
    // Spring Cloud BOM 가져오기 (Gradle 표준 방식)
    // platform()을 사용하면 import 없이도 BOM 버전을 관리할 수 있습니다.
    add("implementation", platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"))
    // 라이브러리 추가 (add 함수 사용)
    add("implementation", "org.springframework.boot:spring-boot-starter-web")
    add("implementation", "org.springframework.cloud:spring-cloud-starter-openfeign")
    // JPA & JDBC
    add("implementation", "org.springframework.boot:spring-boot-starter-data-jpa")
    add("runtimeOnly", "org.postgresql:postgresql")
    // Spring Batch
    add("implementation", "org.springframework.boot:spring-boot-starter-batch")
    // 공통 모듈 (R2DBC 제외)
    add("implementation", project(":common-module")) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-data-r2dbc")
        exclude(group = "io.r2dbc")
        exclude(group = "org.springframework.data", module = "spring-data-r2dbc")
    }
    // 테스트
    add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
    add("implementation", "com.alibaba:easyexcel:3.3.4")

    // [테스트용] Spring Batch Test 도구
    add("testImplementation", "org.springframework.batch:spring-batch-test")
}

tasks.withType<Test> {
    useJUnitPlatform()

    // [핵심] 테스트 실행 중 표준 출력(System.out)과 에러(System.err)를 콘솔에 표시
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}