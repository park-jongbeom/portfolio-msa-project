group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "api-gateway-service"


dependencies {
    // 1. WebFlux (논블로킹 HTTP)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // 2. R2DBC (논블로킹 DB)
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    implementation(project(":common-module"))
}