package com.qk54r71.searchservice.service

import com.qk54r71.commonmodule.domain.dto.DrugSearchResponseDto
import com.qk54r71.searchservice.repository.DrugSearchRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DrugSearchService(
    private val repository: DrugSearchRepository
) {
    // 검색 목록 조회
    fun search(keyword: String, page: Int, size: Int): Flux<DrugSearchResponseDto> {
        return repository.searchDrugs(keyword, page, size)
    }

    // (선택) 전체 개수 조회 - Pagination 정보 만들 때 사용
    fun count(keyword: String): Mono<Long> {
        return repository.countDrugs(keyword)
    }
}