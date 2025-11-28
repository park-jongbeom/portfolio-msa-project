package com.qk54r71.ingestionservice.listener

import com.qk54r71.commonmodule.domain.entity.BatchJobHistory
import com.qk54r71.ingestionservice.repository.BatchJobHistoryRepository
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

@Component
class JobLoggingListener(
    private val batchJobHistoryRepository: BatchJobHistoryRepository
) : JobExecutionListener {

    /**
     * Job 시작 전: 'STARTED' 상태로 기록 생성
     */
    @Transactional
    override fun beforeJob(jobExecution: JobExecution) {
        val history = BatchJobHistory(
            jobName = jobExecution.jobInstance.jobName,
            status = "STARTED",
            startTime = Instant.now(),
            delBl = false
        )
        val savedHistory = batchJobHistoryRepository.save(history)

        // 종료 시 업데이트를 위해 생성된 ID를 ExecutionContext에 저장해둡니다.
        jobExecution.executionContext.putLong("HISTORY_ID", savedHistory.historyId!!)
    }

    /**
     * Job 종료 후: 성공/실패 여부, 건수, 시간 업데이트
     */
    @Transactional
    override fun afterJob(jobExecution: JobExecution) {
        val historyId = jobExecution.executionContext.getLong("HISTORY_ID")
        val history = batchJobHistoryRepository.findById(historyId).orElse(null) ?: return

        val endTime = Instant.now()
        val duration = Duration.between(history.startTime, endTime).toMillis()

        // 처리 건수 합산 (모든 Step의 writeCount 합계)
        val totalProcessed = jobExecution.stepExecutions.sumOf { it.writeCount }

        // 실패 원인 추출 (예외가 있다면)
        val failReason = if (jobExecution.failureExceptions.isNotEmpty()) {
            jobExecution.failureExceptions.joinToString("\n") { it.message ?: "Unknown Error" }
                .take(2000) // DB 컬럼 크기에 맞춰 자르기 (TEXT 타입이면 안 잘라도 됨)
        } else {
            null
        }

        // 객체 업데이트 (JPA Dirty Checking)
        history.apply {
            this.endTime = endTime
            this.status = jobExecution.status.toString() // COMPLETED, FAILED 등
            this.durationMs = duration
            this.processedCount = totalProcessed.toInt()
            this.failReason = failReason
        }

        // save 호출 (명시적 업데이트)
        batchJobHistoryRepository.save(history)
    }
}