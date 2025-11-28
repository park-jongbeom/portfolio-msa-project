package com.qk54r71.ingestionservice.scheduler

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BatchScheduler(
    private val jobLauncher: JobLauncher,
    private val drugInfoUpdateJob: Job
) {
    // 매일 새벽 5시에 실행
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    fun runDailyIngestion() {
        println(">>> [Scheduler] 식약처 데이터 업데이트 배치 시작: ${LocalDateTime.now()}")

        try {
            // 1. Job 파라미터 생성 (중요!)
            // 매일 새로운 Job Instance로 인식되게 하려면, 실행 시점의 시간을 파라미터로 넣어줘야 합니다.
            val jobParameters = JobParametersBuilder()
                .addString("requestDate", LocalDateTime.now().toString())
                .addLong("timestamp", System.currentTimeMillis()) // 확실한 중복 방지용
                .toJobParameters()

            // 2. Job 실행
            jobLauncher.run(drugInfoUpdateJob, jobParameters)

        } catch (e: Exception) {
            println(">>> [Scheduler] 배치 실행 중 오류 발생")
            e.printStackTrace()
        }
    }
}