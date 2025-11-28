package com.qk54r71.commonmodule.domain.entity

import java.time.Instant

data class BatchJobHistory(

    // 1. 기본 키 (Primary Key) 설정: BIGSERIAL 매핑
    val historyId: Long? = null,

    // 2. 배치 정보
    var jobName: String,

    var status: String, // "SUCCESS" 또는 "FAILED"

    val startTime: Instant, // 시작 시간은 불변

    var endTime: Instant? = null, // 종료 시간은 Nullable

    var durationMs: Long? = null,

    var processedCount: Int? = null,

    var failReason: String? = null,

    // 3. 삭제 플래그
    var delBl: Boolean = false, // DB Default FALSE와 일치

    // 4. 생성 시간
    val createdAt: Instant = Instant.now()
)