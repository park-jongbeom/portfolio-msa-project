package com.qk54r71.ingestionservice.config

import com.alibaba.excel.EasyExcel
import com.qk54r71.ingestionservice.tasklet.NedrugDownloadTasklet
import com.qk54r71.ingestionservice.listener.DrugPermitExcelListener
import com.qk54r71.ingestionservice.dto.DrugPermitExcelDto
import com.qk54r71.ingestionservice.listener.JobLoggingListener
import com.qk54r71.ingestionservice.repository.DrugMasterRepository
import com.qk54r71.ingestionservice.repository.DrugSpecRepository
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
            .tasklet({ contribution, chunkContext ->
                // 1. [핵심] 앞 단계에서 저장한 파일 경로를 꺼내옵니다.
                val jobExecutionContext = chunkContext.stepContext.stepExecution.jobExecution.executionContext
                val filePath = jobExecutionContext.getString("DOWNLOAD_FILE_PATH")

                println(">>> [Step 2] 엑셀 데이터 파싱 시작 (파일: $filePath)")

                if (filePath.isNullOrBlank()) {
                    throw RuntimeException("처리할 엑셀 파일 경로가 없습니다.")
                }

                // 2. EasyExcel 실행 (전달받은 filePath 사용)
                EasyExcel.read(
                    filePath,
                    DrugPermitExcelDto::class.java,
                    DrugPermitExcelListener(drugMasterRepository, drugSpecRepository)
                )
                    .sheet()
                    .doRead()

                println(">>> [Step 2] DB 적재 완료.")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}