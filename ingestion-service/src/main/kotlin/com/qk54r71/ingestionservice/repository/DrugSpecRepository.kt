package com.qk54r71.ingestionservice.repository

import com.qk54r71.commonmodule.domain.entity.DrugSpec
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DrugSpecRepository : JpaRepository<DrugSpec, Long> {
    // 마스터 ID로 상세 정보 조회 (업데이트 시 사용)
    fun findByMasterId(masterId: Long): DrugSpec?
}