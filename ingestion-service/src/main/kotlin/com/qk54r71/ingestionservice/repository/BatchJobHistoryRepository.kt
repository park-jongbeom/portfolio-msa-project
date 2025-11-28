package com.qk54r71.ingestionservice.repository

import com.qk54r71.commonmodule.domain.entity.BatchJobHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BatchJobHistoryRepository : JpaRepository<BatchJobHistory, Long> {

}