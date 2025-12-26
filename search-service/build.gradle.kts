plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
}

group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "search-service"


dependencies {
	// === 1. WebFlux & R2DBC (기존 설정 유지) ===
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

	// === 2. 유틸리티 (Coroutines) ===
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// === 3. DB Driver ===
	// runtimeOnly는 컴파일 시점엔 필요 없고 실행 시점에만 필요할 때 씁니다.
	// R2DBC 드라이버는 보통 runtimeOnly가 맞습니다.
	runtimeOnly("org.postgresql:r2dbc-postgresql")

	// === 4. 공통 모듈 ===
	implementation(project(":common-module"))

	// [중요] gRPC 서버 구동용 라이브러리 (이건 서버 프로젝트에만 필요함)
	implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
}

