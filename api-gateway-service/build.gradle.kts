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
    // 3. gRPC Client Starter (핵심)
    // 이 라이브러리가 @GrpcClient 어노테이션을 처리해줍니다.
    implementation("net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE")
    // 4. Coroutines 지원 (비동기 처리를 위해 필수)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // [추가] WebFlux용 Redis (비동기 지원)
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // [추가] JSON 직렬화용 (Redis에 객체를 저장하기 위함)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}