package com.qk54r71.searchservice.config

import com.qk54r71.commonmodule.config.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 모든 예외(Exception)를 잡아서 처리하는 메서드
     */
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, exchange: ServerWebExchange): Mono<ResponseEntity<ErrorResponse>> {
        val path = exchange.request.path.value()
        log.error("💥 [Global Exception] Request Path: $path, Error: ${ex.message}", ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = ex.message ?: "Unknown error occurred",
            path = path
        )

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse))
    }
}