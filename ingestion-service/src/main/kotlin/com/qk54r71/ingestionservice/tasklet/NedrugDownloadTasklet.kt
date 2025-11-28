package com.qk54r71.ingestionservice.tasklet

import com.qk54r71.ingestionservice.client.NedrugClient
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class NedrugDownloadTasklet(
    private val nedrugClient: NedrugClient
) : Tasklet {

    // application.yml에는 이제 "디렉토리 경로"만 적어주세요 (예: /data/download/)
    @Value("\${app.drug-data.file-path}")
    private lateinit var baseDir: String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        println(">>> [Step 1] 식약처 엑셀 다운로드 시작...")

        // 1. 오늘 날짜 기반 파일명 생성 (예: OpenData_ItemPermitDetail20251128.xls)
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val fileName = "OpenData_ItemPermitDetail${todayStr}.xlsx" // 확장자 .xls 주의!

        // 2. 저장할 파일 객체 생성
        val directory = File(baseDir)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val targetFile = File(directory, fileName)

        // 3. Feign Client 호출
        val response = nedrugClient.downloadExcel()

        if (response.status() != 200) {
            throw RuntimeException("다운로드 실패! HTTP Status: ${response.status()}")
        }

        // 4. 파일 저장
        response.body().asInputStream().use { inputStream ->
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        println(">>> 다운로드 완료: ${targetFile.absolutePath}")

        // 5. [핵심] 다음 단계(Step)가 이 파일을 읽을 수 있도록 경로를 Context에 저장
        val jobExecutionContext = chunkContext.stepContext.stepExecution.jobExecution.executionContext
        jobExecutionContext.put("DOWNLOAD_FILE_PATH", targetFile.absolutePath)

        return RepeatStatus.FINISHED
    }
}