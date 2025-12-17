package com.qk54r71.ingestionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration // Import 추가
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

// R2dbcAutoConfiguration 클래스를 exclude(제외) 목록에 추가합니다.
@SpringBootApplication(exclude = [R2dbcAutoConfiguration::class])
@EnableFeignClients
@EnableScheduling
@EnableAsync
class IngestionServiceApplication

fun main(args: Array<String>) {
    runApplication<IngestionServiceApplication>(*args)
}
