package com.qk54r71.ingestionservice

import com.qk54r71.ingestionservice.repository.DrugMasterRepository
import com.qk54r71.ingestionservice.repository.DrugSpecRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test") // application-test.yml 설정 사용
class DrugInfoUpdateJobTest {
    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var drugMasterRepository: DrugMasterRepository

    @Autowired
    private lateinit var drugSpecRepository: DrugSpecRepository

    @Autowired
    private lateinit var drugInfoUpdateJob: Job

    // 테스트 설정 파일에 지정된 다운로드 '디렉토리' 경로
    @Value("\${app.drug-data.file-path}")
    private lateinit var downloadDir: String

    @BeforeEach
    fun setUp() {
        // 1. DB 초기화
        drugSpecRepository.deleteAll()
        drugMasterRepository.deleteAll()

        // 2. 테스트 대상 Job 설정
        jobLauncherTestUtils.job = drugInfoUpdateJob

        // 3. 기존에 다운로드된 테스트 파일이 있다면 삭제 (클린 테스트)
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val fileName = "OpenData_ItemPermitDetail${todayStr}.xlsx"
        val targetFile = File(downloadDir, fileName)

        if (targetFile.exists()) {
            targetFile.delete()
        }
    }

    @Test
    fun `실제_사이트에서_오늘자_엑셀을_다운받아_DB에_적재한다`() {
        // Given
        println(">>> [TEST] 통합 테스트 시작: 실제 다운로드 및 DB 적재")

        // When: Job 실행
        // (Step 1에서 다운로드 -> Context에 경로 저장 -> Step 2에서 경로 읽기 -> DB 저장)
        val jobExecution = jobLauncherTestUtils.launchJob()

        // Then
        // 1. Job 성공 여부 확인
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)

        // 2. 파일이 실제로 다운로드 되었는지 확인
        // (프로덕션 코드와 동일한 로직으로 파일명을 계산해서 확인)
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val fileName = "OpenData_ItemPermitDetail${todayStr}.xlsx"
        val downloadedFile = File(downloadDir, fileName)

        assertTrue(downloadedFile.exists(), "오늘 날짜의 엑셀 파일이 존재해야 한다: ${downloadedFile.absolutePath}")
        assertTrue(downloadedFile.length() > 0, "파일 크기가 0보다 커야 한다.")
        println(">>> 다운로드 확인 완료: ${downloadedFile.name} (${downloadedFile.length()} bytes)")

        // 3. DB 데이터 적재 확인
        val masterCount = drugMasterRepository.count()
        val specCount = drugSpecRepository.count()

        println(">>> DB 적재 결과 - Master: $masterCount, Spec: $specCount")

        assertTrue(masterCount > 0, "DrugMaster 데이터가 적재되어야 한다.")
        assertTrue(specCount > 0, "DrugSpec 데이터가 적재되어야 한다.")

        // Master와 Spec의 개수가 (대략적으로) 맞는지 확인
        // (데이터 무결성에 따라 100% 일치하지 않을 수도 있지만, 여기서는 로직 검증용)
        // assertEquals(masterCount, specCount)
    }
}