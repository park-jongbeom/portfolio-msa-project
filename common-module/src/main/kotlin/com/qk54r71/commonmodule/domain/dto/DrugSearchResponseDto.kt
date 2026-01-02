package com.qk54r71.commonmodule.domain.dto

import java.time.LocalDate

data class DrugSearchResponseDto(
    val itemSeq: String,           // 품목일련번호
    val productNameKo: String,     // 품목명(한글)
    val productNameEn: String?,    // 품목명(영문)
    val companyName: String,       // 업체명
    val itemType: String?,         // 전문/일반
    val mainIngredient: String?,   // 주성분 (Spec 테이블)
    val efficacyUrl: String?,      // 효능효과 URL (Spec 테이블)
    val dosageUrl: String?,        // 용법용량 URL (Spec 테이블)
    val permitDate: String?     // 허가일자
)