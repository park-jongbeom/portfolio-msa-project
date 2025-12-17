package com.qk54r71.ingestionservice.config

import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.launch.JobOperator
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.stereotype.Component

@Component
class GracefulShutdownHandler(
    private val jobExplorer: JobExplorer,
    private val jobOperator: JobOperator
) : ApplicationListener<ContextClosedEvent> {

    override fun onApplicationEvent(event: ContextClosedEvent) {
        // 1. 현재 실행 중인 모든 Job을 찾음
        val jobNames = jobExplorer.jobNames
        for (jobName in jobNames) {
            val runningExecutions = jobExplorer.findRunningJobExecutions(jobName)

            for (execution in runningExecutions) {
                try {
                    // 2. 해당 Job에게 "Stop" 신호를 보냄
                    // 이 명령을 받으면 배치는 '현재 진행 중인 청크'까지만 끝내고 멈춥니다.
                    jobOperator.stop(execution.id)
                    println("🛑 Graceful Shutdown: Job [${execution.jobInstance.jobName}] stop request sent.")
                } catch (e: Exception) {
                    println("⚠️ Failed to stop job: ${e.message}")
                }
            }
        }
    }
}