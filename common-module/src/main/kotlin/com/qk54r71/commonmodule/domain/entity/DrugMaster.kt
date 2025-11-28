package com.qk54r71.commonmodule.domain.entity

import java.time.Instant

// DB 어노테이션을 모두 제거하고 순수한 데이터 클래스만 남깁니다.
// R2DBC용 @Table 어노테이션 (@Table("drug_master")) 제거
data class DrugMaster(

    // 1. 시스템 내부 ID (PK)
    val masterId: Long? = null,

    // 2. 비즈니스 키 (CDC 기준 - 품목일련번호)
    var itemSeq: String,

    // 3. 기본 정보
    var productNameKo: String,

    var productNameEn: String? = null,

    var companyName: String,

    var companyNameEn: String? = null,

    var itemType: String? = null, // 전문/일반

    // 4. 상태 및 날짜 정보
    var permitDate: String? = null, // 허가일자

    var cancelDate: String? = null, // 취소일자

    var cancelStatus: String? = null, // 취소상태

    // 5. 검색 최적화 (TSVECTOR - String 처리)
    val searchVector: String? = null,

    // 6. 관리 필드
    var delBl: Boolean = false,

    val createdAt: Instant = Instant.now(),

    var updatedAt: Instant = Instant.now()
)