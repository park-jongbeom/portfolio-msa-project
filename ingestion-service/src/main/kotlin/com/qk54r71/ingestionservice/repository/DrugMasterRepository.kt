package com.qk54r71.ingestionservice.repository

import com.qk54r71.commonmodule.domain.entity.DrugMaster
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DrugMasterRepository : JpaRepository<DrugMaster, Long> {
    // CDC(변경 감지)를 위해 품목일련번호로 기존 데이터 조회
    fun findByItemSeq(itemSeq: String): DrugMaster?
}