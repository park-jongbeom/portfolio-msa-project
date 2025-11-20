// build.gradle.kts (루트)
plugins {
    // 버전 정보만 선언하고 적용(apply)은 하지 않음
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
}

// 각 하위 모듈에 공통적으로 적용할 설정만 남깁니다.
subprojects {
    repositories {
        mavenCentral()
    }

    // Java Toolchain 설정은 개별 모듈에 남겨둡니다.
    // 다른 공통 설정을 여기에 추가할 수 있습니다.
}