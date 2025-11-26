group = "com.qk54r71"
version = "0.0.1-SNAPSHOT"
description = "common-module"

dependencies {
    // R2DBC 코어 종속성 (엔티티 정의를 위해 필요)
    implementation("org.springframework.data:spring-data-r2dbc")
    // PostgreSQL JDBC Driver
    runtimeOnly("org.postgresql:postgresql")
}