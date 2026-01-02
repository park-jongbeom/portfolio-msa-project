package com.qk54r71.searchservice.repository

import com.qk54r71.commonmodule.domain.dto.DrugSearchResponseDto
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class DrugSearchRepository(
    private val client: DatabaseClient
) {
    /**
     * 키워드 검색 (품목명, 업체명, 성분명 포함)
     * Limit, Offset을 이용한 페이징 처리
     */
    fun searchDrugs(keyword: String, page: Int, size: Int): Flux<DrugSearchResponseDto> {
        // 검색어가 비어있으면 전체 조회, 있으면 조건 검색
        val whereClause = if (keyword.isBlank()) {
            "1 = 1" // 참인 조건 (전체)
        } else {
            """
            (m.product_name_ko LIKE :keyword 
             OR m.company_name LIKE :keyword 
             OR s.main_ingredient LIKE :keyword)
            """.trimIndent()
        }

        val sql = """
            SELECT 
                m.item_seq, 
                m.product_name_ko, 
                m.product_name_en, 
                m.company_name, 
                m.item_type, 
                m.permit_date,
                s.main_ingredient, 
                s.efficacy_url, 
                s.dosage_url
            FROM drug_master m
            LEFT JOIN drug_spec s ON m.master_id = s.master_id
            WHERE $whereClause
            ORDER BY m.permit_date DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        // Bind & Execute
        var spec = client.sql(sql)
            .bind("limit", size)
            .bind("offset", (page - 1) * size)

        if (keyword.isNotBlank()) {
            spec = spec.bind("keyword", "%$keyword%")
        }

        return spec.map { row, _ ->
            DrugSearchResponseDto(
                itemSeq = row.get("item_seq", String::class.java) ?: "",
                productNameKo = row.get("product_name_ko", String::class.java) ?: "",
                productNameEn = row.get("product_name_en", String::class.java),
                companyName = row.get("company_name", String::class.java) ?: "",
                itemType = row.get("item_type", String::class.java),
                mainIngredient = row.get("main_ingredient", String::class.java),
                efficacyUrl = row.get("efficacy_url", String::class.java),
                dosageUrl = row.get("dosage_url", String::class.java),
                permitDate = row.get("permit_date", String::class.java)
            )
        }.all()
    }

    /**
     * 전체 검색 결과 개수 (페이징 메타데이터용)
     */
    fun countDrugs(keyword: String): Mono<Long> {
        val whereClause = if (keyword.isBlank()) "1 = 1" else
            "(m.product_name_ko LIKE :keyword OR m.company_name LIKE :keyword OR s.main_ingredient LIKE :keyword)"

        val sql = """
            SELECT count(*) 
            FROM drug_master m
            LEFT JOIN drug_spec s ON m.item_seq = s.master_id
            WHERE $whereClause
        """.trimIndent()

        var spec = client.sql(sql)
        if (keyword.isNotBlank()) {
            spec = spec.bind("keyword", "%$keyword%")
        }

        return spec.map { row, _ -> row.get(0, Long::class.java) ?: 0L }.one()
    }
}