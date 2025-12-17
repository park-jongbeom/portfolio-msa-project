package com.qk54r71.ingestionservice.config
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobOperator
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class AutoJobRestarter(
    private val jobExplorer: JobExplorer,
    private val jobOperator: JobOperator,
    private val jobRegistry: JobRegistry
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val jobNames = jobRegistry.jobNames

        for (jobName in jobNames) {
            // 1. 해당 잡의 가장 최근 실행 기록을 가져옴
            val lastInstance = jobExplorer.getLastJobInstance(jobName) ?: continue
            val lastExecution = jobExplorer.getLastJobExecution(lastInstance) ?: continue

            // 2. 상태가 'STOPPED' (배포 때문에 중단됨) 혹은 'FAILED' 인지 확인
            if (lastExecution.status == BatchStatus.STOPPED || lastExecution.status == BatchStatus.FAILED) {
                println("🔄 Found stopped job: [${jobName}], ID: ${lastExecution.id}. Restarting...")

                try {
                    // 3. 멈춘 지점부터 재시작 (Restart)
                    jobOperator.restart(lastExecution.id)
                    println("✅ Job [${jobName}] restarted successfully.")
                } catch (e: Exception) {
                    println("⚠️ Failed to restart job: ${e.message}")
                }
            }
        }
    }
}