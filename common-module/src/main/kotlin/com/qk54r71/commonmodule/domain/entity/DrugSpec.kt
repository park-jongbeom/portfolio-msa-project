package com.qk54r71.commonmodule.domain.entity

import java.time.Instant

// DB의 'drug_spec' 테이블과 매핑되는 엔티티
data class DrugSpec(

    // 1. 시스템 내부 ID (PK)
    val specId: Long? = null,

    // 2. 마스터 테이블 참조 ID (FK)
    var masterId: Long = Long.MIN_VALUE,

    // 3. 각종 식별 코드
    var standardCode: String? = null, // 표준코드

    var insuranceCode: String? = null, // 보험코드

    var atcCode: String? = null,

    // 4. 상세 의약학 정보
    var mainIngredient: String? = null, // 주성분명 (TEXT)

    var additives: String? = null, // 첨가제명

    var appearance: String? = null, // 성상

    // 5. URL 링크 정보 (효능, 용법, 주의사항)
    var efficacyUrl: String? = null,

    var dosageUrl: String? = null,

    var precautionsUrl: String? = null,

    // 6. 보관 및 기타 정보
    var storageMethod: String? = null,

    var validPeriod: String? = null,

    var packingUnit: String? = null,

    var businessType: String? = null, // 업종구분

    var newDrugYn: String? = null, // 신약여부

    // 7. 기타 확장 데이터 (JSONB)
    var etcData: String? = null,

    // 8. 관리 필드
    var delBl: Boolean = false,

    val createdAt: Instant = Instant.now(),

    var updatedAt: Instant = Instant.now()
)