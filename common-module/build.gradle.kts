group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "common-module"

dependencies {
    // R2DBC 코어 종속성 (엔티티 정의를 위해 필요)
    implementation("org.springframework.data:spring-data-r2dbc")
    // PostgreSQL JDBC Driver
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    // JPA 관련 어노테이션 (@Entity, @Table 등) 사용을 위한 의존성
    // 이 모듈은 실행 가능한 애플리케이션이 아니므로 'starter' 대신 'api'만 사용
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
}