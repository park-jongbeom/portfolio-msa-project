import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.protobuf") version "0.9.4"
    `java-library`
}

group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "common-module"

// 버전 변수
val grpcVersion = "1.63.0"
val grpcKotlinVersion = "1.4.1"
val protobufVersion = "3.25.1"

dependencies {
    // R2DBC & PostgreSQL
    implementation("org.springframework.data:spring-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // JPA API (실행용 아님)
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // gRPC API (다른 모듈에 전파)
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    api("com.google.protobuf:protobuf-kotlin:4.28.2")
    api("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

// 1. [필수] 생성된 gRPC 코드를 소스 디렉토리로 인식시키는 설정
// IntelliJ가 build/generated 폴더를 자동으로 'Source Root'로 인식하게 만듭니다.
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
            srcDirs("build/generated/source/proto/main/kotlin")
            srcDirs("build/generated/source/proto/main/grpckt")
        }
    }
}

// 2. [필수] 공통 모듈을 라이브러리로 만들기 (실행 가능한 Jar가 아님)
// Spring Boot 플러그인은 기본적으로 실행 가능한 'bootJar'를 만드는데,
// 다른 모듈에서 참조하려면 일반 'jar'가 필요합니다.
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
    // 'plain' classifier를 없애서 일반 jar 이름으로 생성되게 함
    archiveClassifier.set("")
}