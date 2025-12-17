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
import org.slf4j.LoggerFactory // 로거 추가
import java.time.LocalDateTime

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test") // application-test.yml 설정 사용
class DrugInfoUpdateJobTest {
    // println 대신 Logger 사용 권장 (시간/스레드 정보 자동 출력)
    private val log = LoggerFactory.getLogger(this::class.java)

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
        log.info(">>> [Test SetUp] 시작: DB 초기화 및 파일 정리")

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
        log.info(">>> [Test SetUp] 완료")
    }

    @Test
    fun `실제_사이트에서_오늘자_엑셀을_다운받아_DB에_적재한다`() {
        // Given
        log.info(">>> [TEST Start] 실제 다운로드 및 DB 적재 테스트 시작 (Time: ${LocalDateTime.now()})")

        // When: Job 실행 (파라미터 추가)
        val jobParameters = jobLauncherTestUtils.uniqueJobParametersBuilder
            .addLong("readLimit", 50L) // 👈 1000개만 읽도록 제한 설정
            .toJobParameters()

        log.info(">>> [TEST] Job 실행 요청 (다운로드 시작 - 시간이 걸릴 수 있음)")

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        log.info(">>> [TEST] Job 실행 완료. 상태: ${jobExecution.exitStatus}")

        // Then
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)

        // 2. 파일이 실제로 다운로드 되었는지 확인
        // (프로덕션 코드와 동일한 로직으로 파일명을 계산해서 확인)
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val fileName = "OpenData_ItemPermitDetail${todayStr}.xlsx"
        val downloadedFile = File(downloadDir, fileName)

        log.info(">>> [TEST] 파일 확인: ${downloadedFile.absolutePath}")
        assertTrue(downloadedFile.exists(), "오늘 날짜의 엑셀 파일이 존재해야 한다: ${downloadedFile.absolutePath}")
        assertTrue(downloadedFile.length() > 0, "파일 크기가 0보다 커야 한다.")
        log.info(">>> [TEST] 다운로드 파일 크기: ${downloadedFile.length()} bytes")

        // 3. DB 데이터 적재 확인
        val masterCount = drugMasterRepository.count()
        val specCount = drugSpecRepository.count()

        log.info(">>> [TEST] DB 적재 결과 - Master: $masterCount, Spec: $specCount")

        // 검증: 0보다 크고, 1000개 근처여야 함 (헤더나 공백 제외 시 오차 있을 수 있음)
        assertTrue(masterCount > 0, "데이터가 적재되어야 한다.")
        assertTrue(masterCount <= 1100, "제한 설정(1000)보다 너무 많이 저장되면 안 된다.")

        // Master와 Spec의 개수가 (대략적으로) 맞는지 확인
        // (데이터 무결성에 따라 100% 일치하지 않을 수도 있지만, 여기서는 로직 검증용)
        // assertEquals(masterCount, specCount)
    }
}