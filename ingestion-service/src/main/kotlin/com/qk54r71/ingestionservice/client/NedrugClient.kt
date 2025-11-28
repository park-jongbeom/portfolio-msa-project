package com.qk54r71.ingestionservice.client

import feign.Response
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "nedrugClient", url = "https://nedrug.mfds.go.kr")
interface NedrugClient {

    /**
     * 식약처 의약품 허가 목록 엑셀 다운로드
     * URL: https://nedrug.mfds.go.kr/cmn/xls/down/OpenData_ItemPermitDetail
     * Method: GET
     */
    @GetMapping(value = ["/cmn/xls/down/OpenData_ItemPermitDetail"])
    fun downloadExcel(
        // 공공기관 사이트 봇 차단 방지용 헤더 (브라우저로 위장)
        @RequestHeader("User-Agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    ): Response
}