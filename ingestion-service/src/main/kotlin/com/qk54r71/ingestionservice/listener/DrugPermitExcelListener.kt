package com.qk54r71.ingestionservice.listener

import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.read.listener.ReadListener
import com.qk54r71.commonmodule.domain.entity.DrugMaster
import com.qk54r71.commonmodule.domain.entity.DrugSpec
import com.qk54r71.ingestionservice.dto.DrugPermitExcelDto
import com.qk54r71.ingestionservice.repository.DrugMasterRepository
import com.qk54r71.ingestionservice.repository.DrugSpecRepository
import org.slf4j.LoggerFactory

/**
 * 엑셀 데이터를 읽어서 DB에 적재하는 리스너
 * (Upsert 로직 포함: 기존 데이터가 있으면 업데이트, 없으면 생성)
 */
class DrugPermitExcelListener(
    private val drugMasterRepository: DrugMasterRepository,
    private val drugSpecRepository: DrugSpecRepository
) : ReadListener<DrugPermitExcelDto> {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val BATCH_COUNT = 100 // 한 번에 처리할 데이터 수 (메모리 관리용)
    private val cachedList = ArrayList<DrugPermitExcelDto>()

    /**
     * 엑셀의 한 행(Row)을 읽을 때마다 호출됨
     */
    override fun invoke(data: DrugPermitExcelDto, context: AnalysisContext) {
        // 품목일련번호(Key)가 없는 데이터는 스킵
        if (!data.itemSeq.isNullOrBlank()) {
            cachedList.add(data)
        }

        // 버퍼가 꽉 차면 DB에 저장하고 비움
        if (cachedList.size >= BATCH_COUNT) {
            saveData()
            cachedList.clear()
        }
    }

    /**
     * 모든 데이터 읽기가 끝난 후 호출됨 (남은 데이터 처리)
     */
    override fun doAfterAllAnalysed(context: AnalysisContext) {
        saveData()
        log.info(">>> 모든 엑셀 데이터 파싱 및 적재 완료.")
    }

    /**
     * 실제 DB 저장 로직 (Master / Spec 분리 저장)
     * 주의: 이 클래스는 Spring Bean이 아니므로 @Transactional이 동작하지 않습니다.
     * Repository의 save()는 자체적으로 트랜잭션을 가집니다.
     */
    private fun saveData() {
        if (cachedList.isEmpty()) return

        log.info(">>> ${cachedList.size}건 데이터 DB 처리 중...")

        for (dto in cachedList) {
            try {
                processUpsert(dto)
            } catch (e: Exception) {
                // 🚨 에러 발생 시 상세 분석 로그 출력
                logErrorDetail(dto, e)

                // 분석 후에는 테스트가 실패하도록 예외를 다시 던집니다.
                // (배치 중단 방지를 원하시면 throw 대신 continue를 쓸 수 있지만,
                // 지금은 원인 파악이 우선이므로 throw 합니다.)
                throw e
            }
        }
    }

    /**
     * 범인 색출을 위한 로그 출력 함수
     */
    private fun logErrorDetail(dto: DrugPermitExcelDto, e: Exception) {
        log.error("🛑 [DB 저장 실패] ItemSeq: ${dto.itemSeq}")
        log.error("🛑 에러 메시지: ${e.message}")
        log.error("====== [컬럼 데이터 길이 분석] ======")

        // 주요 문자열 컬럼들의 길이를 찍어봅니다. (길이가 긴 순서대로 의심해봐야 함)
        printLen("품목명(Ko)", dto.productNameKo)
        printLen("품목명(En)", dto.productNameEn)
        printLen("업체명", dto.companyName)
        printLen("주성분명", dto.mainIngredient)
        printLen("효능효과 URL", dto.efficacyUrl)
        printLen("용법용량 URL", dto.dosageUrl)
        printLen("주의사항 URL", dto.precautionsUrl)
        printLen("성상", dto.appearance)
        printLen("첨가제", dto.additives)
        printLen("저장방법", dto.storageMethod)

        log.error("===================================")
    }

    private fun printLen(fieldName: String, value: String?) {
        val len = value?.length ?: 0
        // 길이가 0보다 큰 경우에만, 혹은 100자 넘는 경우만 강조해서 출력
        if (len > 0) {
            // 값이 너무 길면 로그가 지저분해지므로 앞부분만 잘라서 보여줌
            val preview = if (len > 50) value?.substring(0, 50) + "..." else value
            log.error("👉 [$fieldName] 길이: $len | 값: $preview")
        }
    }

    private fun processUpsert(dto: DrugPermitExcelDto) {
        val itemSeq = dto.itemSeq!! // 위에서 null 체크 함

        // ==========================================
        // 1. DrugMaster 처리 (기본 정보)
        // ==========================================
        var master = drugMasterRepository.findByItemSeq(itemSeq)

        if (master == null) {
            // [INSERT] 신규 생성
            master = DrugMaster(
                itemSeq = itemSeq,
                productNameKo = dto.productNameKo ?: "",
                productNameEn = dto.productNameEn,
                companyName = dto.companyName ?: "",
                companyNameEn = dto.companyNameEn,
                itemType = dto.itemType,
                permitDate = dto.permitDate,
                cancelDate = dto.cancelDate,
                cancelStatus = dto.cancelStatus
            )
        } else {
            // [UPDATE] 기존 데이터 갱신 (변경사항 반영)
            master.apply {
                productNameKo = dto.productNameKo ?: ""
                productNameEn = dto.productNameEn
                companyName = dto.companyName ?: ""
                companyNameEn = dto.companyNameEn
                itemType = dto.itemType
                permitDate = dto.permitDate
                cancelDate = dto.cancelDate
                cancelStatus = dto.cancelStatus
                // 필요한 필드 계속 업데이트...
            }
        }
        // Master 저장 및 ID 반환
        val savedMaster = drugMasterRepository.save(master)


        // ==========================================
        // 2. DrugSpec 처리 (상세 정보)
        // ==========================================
        // Master ID로 기존 Spec 조회
        var spec = drugSpecRepository.findByMasterId(savedMaster.masterId!!)

        if (spec == null) {
            // [INSERT] 신규 생성
            spec = DrugSpec(
                masterId = savedMaster.masterId!!, // FK 설정
                standardCode = dto.standardCode,
                insuranceCode = dto.insuranceCode,
                atcCode = dto.atcCode,
                mainIngredient = dto.mainIngredient,
                additives = dto.additives,
                appearance = dto.appearance,
                efficacyUrl = dto.efficacyUrl,
                dosageUrl = dto.dosageUrl,
                precautionsUrl = dto.precautionsUrl,
                storageMethod = dto.storageMethod,
                validPeriod = dto.validPeriod,
                packingUnit = dto.packingUnit,
                businessType = dto.businessType,
                newDrugYn = dto.newDrugYn
            )
        } else {
            // [UPDATE] 기존 데이터 갱신
            spec.apply {
                standardCode = dto.standardCode
                insuranceCode = dto.insuranceCode
                atcCode = dto.atcCode
                mainIngredient = dto.mainIngredient
                additives = dto.additives
                appearance = dto.appearance
                efficacyUrl = dto.efficacyUrl
                dosageUrl = dto.dosageUrl
                precautionsUrl = dto.precautionsUrl
                storageMethod = dto.storageMethod
                validPeriod = dto.validPeriod
                packingUnit = dto.packingUnit
                businessType = dto.businessType
                newDrugYn = dto.newDrugYn
            }
        }
        // Spec 저장
        drugSpecRepository.save(spec)
    }
}