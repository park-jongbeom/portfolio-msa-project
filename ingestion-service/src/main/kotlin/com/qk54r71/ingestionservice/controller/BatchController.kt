package com.qk54r71.ingestionservice.controller

import com.qk54r71.ingestionservice.scheduler.BatchScheduler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val batchScheduler: BatchScheduler // 위에서 수정한 스케줄러 주입
) {
    @PostMapping("/trigger-batch")
    fun triggerBatch(): ResponseEntity<String> {
        batchScheduler.runJob()
        return ResponseEntity.ok("✅ 배치 작업이 백그라운드에서 시작되었습니다.")
    }
}