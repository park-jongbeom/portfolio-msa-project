package com.qk54r71.ingestionservice.config

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.exception.ExcelAnalysisStopException
import com.qk54r71.ingestionservice.dto.DrugPermitExcelDto
import com.qk54r71.ingestionservice.listener.DrugPermitExcelListener
import com.qk54r71.ingestionservice.listener.JobLoggingListener
import com.qk54r71.ingestionservice.repository.DrugMasterRepository
import com.qk54r71.ingestionservice.repository.DrugSpecRepository
import com.qk54r71.ingestionservice.tasklet.NedrugDownloadTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val nedrugDownloadTasklet: NedrugDownloadTasklet,
    private val drugMasterRepository: DrugMasterRepository,
    private val drugSpecRepository: DrugSpecRepository,
    private val jobLoggingListener: JobLoggingListener
) {

    @Bean
    fun drugInfoUpdateJob(): Job {
        return JobBuilder("drugInfoUpdateJob", jobRepository)
            .listener(jobLoggingListener)
            .start(fileDownloadStep())
            .next(excelToDbStep())
            .build()
    }

    @Bean
    fun fileDownloadStep(): Step {
        return StepBuilder("fileDownloadStep", jobRepository)
            .tasklet(nedrugDownloadTasklet, transactionManager)
            .build()
    }

    @Bean
    fun excelToDbStep(): Step {
        return StepBuilder("excelToDbStep", jobRepository)
            .tasklet({ _, chunkContext ->
                // 1. [핵심] 앞 단계에서 저장한 파일 경로를 꺼내옵니다.
                val jobExecutionContext = chunkContext.stepContext.stepExecution.jobExecution.executionContext
                val filePath = jobExecutionContext.getString("DOWNLOAD_FILE_PATH")

                //JobParameter에서 limit 값 읽기 (없으면 -1)
                val jobParams = chunkContext.stepContext.jobParameters
                val readLimit = jobParams["readLimit"]?.toString()?.toLong() ?: -1L

                println(">>> [Step 2] 엑셀 파싱 시작 (파일: $filePath, 제한: $readLimit)")

                if (filePath.isNotBlank()) {
                    try {
                        EasyExcel.read(
                            filePath,
                            DrugPermitExcelDto::class.java,
                            // Listener에 limit 값 전달
                            DrugPermitExcelListener(
                                drugMasterRepository,
                                drugSpecRepository,
                                transactionManager,
                                readLimit
                            )
                        )
                            .sheet()
                            .doRead()
                    } catch (e: ExcelAnalysisStopException) {
                        // [핵심] 의도된 중단이므로 예외를 삼키고 정상 종료 처리
                        println(">>> 설정된 제한 개수($readLimit)만큼 처리하고 중단했습니다.")
                    }
                }

                println(">>> [Step 2] DB 적재 완료.")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}