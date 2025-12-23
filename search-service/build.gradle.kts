group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "search-service"

dependencies {
	// 1. WebFlux (논블로킹 HTTP)
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	// 2. R2DBC (논블로킹 DB)
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	// 3. 유틸리티 (Coroutines 지원 - 선택사항이지만 추천)
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	implementation(project(":common-module"))
}
