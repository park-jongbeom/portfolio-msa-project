package com.qk54r71.apigatewayservice.controller

import com.qk54r71.apigatewayservice.service.DrugSearchClientService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/gateway/v1") // Gateway 전용 URL prefix
class GatewaySearchController(
    private val drugSearchClientService: DrugSearchClientService
) {

    /**
     * [HTTP] GET /api/gateway/v1/drugs
     * Gateway -> (gRPC) -> Search Service -> (R2DBC) -> DB
     */
    @GetMapping("/drugs")
    suspend fun searchDrugs(
        @RequestParam(defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Map<String, Any> {

        val resultList = drugSearchClientService.searchDrugs(keyword, page, size)

        // 응답 포맷 통일
        return mapOf(
            "success" to true,
            "count" to resultList.size,
            "data" to resultList
        )
    }
}