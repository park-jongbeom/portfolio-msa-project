package com.qk54r71.apigatewayservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.qk54r71.commonmodule.grpc.DrugSearchRequest
import com.qk54r71.commonmodule.grpc.DrugSearchServiceGrpcKt
import kotlinx.coroutines.reactor.awaitSingleOrNull
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class DrugSearchClientService(
    // [추가] Redis 템플릿과 Jackson ObjectMapper 주입
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    @GrpcClient("search-service")
    private lateinit var drugSearchStub: DrugSearchServiceGrpcKt.DrugSearchServiceCoroutineStub

    suspend fun searchDrugs(keyword: String, page: Int, size: Int): List<Map<String, Any>> {
        // 1. Redis Key 생성 (예: "search:타이레놀:1:10")
        val cacheKey = "search:$keyword:$page:$size"

        // 2. [Redis 조회] 캐시가 있는지 확인
        val cachedValue = redisTemplate.opsForValue().get(cacheKey).awaitSingleOrNull()

        if (cachedValue != null) {
            println("✅ Cache Hit! (Redis에서 반환): $cacheKey")
            // JSON String -> List<Map> 변환 후 반환
            return objectMapper.readValue(cachedValue)
        }

        println("🚀 Cache Miss! (gRPC 호출): $cacheKey")

        // 3. [gRPC 호출] 데이터가 없으면 서버에 요청
        val request = DrugSearchRequest.newBuilder()
            .setKeyword(keyword)
            .setPage(page)
            .setSize(size)
            .build()

        val response = drugSearchStub.searchDrugs(request)

        // 4. 데이터 변환 (Proto -> Map)
        val resultList = response.dataList.map { proto ->
            mapOf(
                "itemSeq" to proto.itemSeq,
                "productNameKo" to proto.productNameKo,
                "productNameEn" to proto.productNameEn,
                "companyName" to proto.companyName,
                "itemType" to proto.itemType,
                "mainIngredient" to proto.mainIngredient,
                "efficacyUrl" to proto.efficacyUrl,
                "dosageUrl" to proto.dosageUrl,
                "permitDate" to proto.permitDate
            )
        }

        // 5. [Redis 저장] 조회된 결과를 Redis에 저장 (TTL: 10분 설정)
        // 빈 리스트가 아닐 때만 캐싱하는 것이 좋음
        if (resultList.isNotEmpty()) {
            val jsonString = objectMapper.writeValueAsString(resultList)
            redisTemplate.opsForValue().set(cacheKey, jsonString, Duration.ofMinutes(10)).awaitSingleOrNull()
        }

        return resultList
    }
}