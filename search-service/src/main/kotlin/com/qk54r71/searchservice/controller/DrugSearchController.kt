package com.qk54r71.searchservice.controller

import com.qk54r71.commonmodule.domain.dto.DrugSearchResponseDto
import com.qk54r71.searchservice.service.DrugSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class DrugSearchController(
    private val drugSearchService: DrugSearchService
) {

    /**
     * GET /api/v1/search/drugs?keyword=타이레놀&page=1&size=10
     */
    @GetMapping("/api/v1/search/drugs")
    fun searchDrugs(
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Flux<DrugSearchResponseDto> {
        // Service의 search 메서드를 호출하여 결과를 리턴합니다.
        // WebFlux가 Flux<T>를 감지하여 JSON Stream으로 응답을 내려줍니다.
        return drugSearchService.search(keyword, page, size)
    }

    /**
     * GET /health
     */
    @GetMapping("/health")
    fun healthCheck(): Mono<String> {
        return Mono.just("Search Service is Alive!")
    }
}