import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// build.gradle.kts (루트)
plugins {
    // 버전 정보만 선언하고 적용(apply)은 하지 않음
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
}

// 각 하위 모듈에 공통적으로 적용할 설정만 남깁니다.
subprojects {
    // 1. 공통 플러그인 적용 (버전 정보는 루트에서 가져옴)
    apply(plugin = "java-library") // Java 프로젝트의 기본 기능 활성화
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    // 2. 공통 Repository 설정 (중복 제거)
    repositories {
        mavenCentral()
    }

    // 3. 공통 Java Toolchain 설정 (중복 제거)
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    // 4. 공통 Kotlin 컴파일러 설정 (중복 제거)
    configure<KotlinJvmProjectExtension> { // kotlin-jvm 플러그인이 제공하는 확장 기능에 접근
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    // 5. 공통 Test 설정 (중복 제거)
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // 6. 공통 Dependencies (모든 모듈이 사용하는 의존성)
    dependencies {
        // 모든 모듈이 사용하는 코틀린 리플렉션 및 테스트 종속성
        add("implementation", "org.springframework.boot:spring-boot-starter")
        add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
        add("testImplementation", "org.jetbrains.kotlin:kotlin-test-junit5")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }
}