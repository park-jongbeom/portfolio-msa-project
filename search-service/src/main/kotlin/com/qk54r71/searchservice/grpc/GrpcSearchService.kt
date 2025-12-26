package com.qk54r71.searchservice.grpc

import com.qk54r71.commonmodule.domain.dto.DrugSearchResponseDto
import com.qk54r71.commonmodule.grpc.DrugInfoData
import com.qk54r71.commonmodule.grpc.DrugSearchRequest
import com.qk54r71.commonmodule.grpc.DrugSearchResponse
import com.qk54r71.commonmodule.grpc.DrugSearchServiceGrpcKt
import com.qk54r71.searchservice.service.DrugSearchService
import net.devh.boot.grpc.server.service.GrpcService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow

@GrpcService // 👈 이 어노테이션이 있어야 gRPC 서버로 등록됨
class GrpcSearchService(
    private val drugSearchService: DrugSearchService // 이전에 만든 서비스 주입
) : DrugSearchServiceGrpcKt.DrugSearchServiceCoroutineImplBase() {

    /**
     * gRPC 메서드 오버라이드 (suspend 함수로 Non-blocking 지원)
     */
    override suspend fun searchDrugs(request: DrugSearchRequest): DrugSearchResponse {
        // 1. 기존 서비스 호출 (Flux -> Flow 변환)
        val fluxResult = drugSearchService.search(
            keyword = request.keyword,
            page = request.page,
            size = request.size
        )

        // 2. Flux 데이터를 Coroutine Flow로 변환 후 리스트로 수집
        // (R2DBC의 Non-blocking 특성을 유지하며 데이터를 가져옴)
        val dtoList: List<DrugSearchResponseDto> = fluxResult.asFlow().toList()

        // 3. DTO -> Proto Message 변환
        val grpcDataList = dtoList.map { dto ->
            DrugInfoData.newBuilder()
                .setItemSeq(dto.itemSeq)
                .setProductNameKo(dto.productNameKo)
                .setProductNameEn(dto.productNameEn ?: "") // Null 처리
                .setCompanyName(dto.companyName)
                .setItemType(dto.itemType ?: "")
                .setMainIngredient(dto.mainIngredient ?: "")
                .setEfficacyUrl(dto.efficacyUrl ?: "")
                .setDosageUrl(dto.dosageUrl ?: "")
                .setPermitDate(dto.permitDate?.toString() ?: "") // LocalDate -> String
                .build()
        }

        // 4. 최종 응답 객체 빌드
        return DrugSearchResponse.newBuilder()
            .addAllData(grpcDataList)
            .setTotalCount(grpcDataList.size.toLong()) // 필요시 count 쿼리 별도 수행
            .build()
    }
}