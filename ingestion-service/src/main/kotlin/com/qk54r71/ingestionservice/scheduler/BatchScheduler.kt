package com.qk54r71.ingestionservice.scheduler

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import org.slf4j.LoggerFactory // 로거 추가
import org.springframework.scheduling.annotation.Async

@Component
class BatchScheduler(
    private val jobLauncher: JobLauncher,
    private val drugInfoUpdateJob: Job
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // 1. [스케줄러용] 매일 새벽 5시에 runJob()을 호출
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    fun runDailyIngestion() {
        log.info("⏰ 스케줄러에 의해 배치가 실행됩니다.")
        runJob() // 아래 공용 메서드 호출
    }

    @Async
    fun runJob() {
        log.info(">>> [Batch] 식약처 데이터 업데이트 배치 시작: ${LocalDateTime.now()}")

        try {
            // Job 파라미터 생성 (중복 실행 방지용 timestamp 필수)
            val jobParameters = JobParametersBuilder()
                .addString("requestDate", LocalDateTime.now().toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()

            // Job 실행
            jobLauncher.run(drugInfoUpdateJob, jobParameters)

        } catch (e: Exception) {
            log.error(">>> [Batch] 배치 실행 중 오류 발생", e)
        }
    }
}