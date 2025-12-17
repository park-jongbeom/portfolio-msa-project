package com.qk54r71.ingestionservice.dto

import com.alibaba.excel.annotation.ExcelProperty

/**
 * 식약처 의약품 허가 목록 전체 엑셀 매핑 DTO
 * (총 36개 컬럼 매핑)
 */
data class DrugPermitExcelDto(

    // 0. 품목명
    @ExcelProperty(index = 0)
    var productNameKo: String? = "",

    // 1. 품목 영문명
    @ExcelProperty(index = 1)
    var productNameEn: String? = "",

    // 2. 품목일련번호 (Key)
    @ExcelProperty(index = 2)
    var itemSeq: String? = "",

    // 3. 허가신고구분
    @ExcelProperty(index = 3)
    var permitType: String? = "",

    // 4. 취소상태
    @ExcelProperty(index = 4)
    var cancelStatus: String? = "",

    // 5. 취소일자
    @ExcelProperty(index = 5)
    var cancelDate: String? = "",

    // 6. 변경일자
    @ExcelProperty(index = 6)
    var changeDate: String? = "",

    // 7. 업체명
    @ExcelProperty(index = 7)
    var companyName: String? = "",

    // 8. 업체 영문명
    @ExcelProperty(index = 8)
    var companyNameEn: String? = "",

    // 9. 허가일자
    @ExcelProperty(index = 9)
    var permitDate: String? = "",

    // 10. 업체허가번호
    @ExcelProperty(index = 10)
    var companyPermitNum: String? = "",

    // 11. 전문일반구분
    @ExcelProperty(index = 11)
    var itemType: String? = "",

    // 12. 성상
    @ExcelProperty(index = 12)
    var appearance: String? = "",

    // 13. 표준코드
    @ExcelProperty(index = 13)
    var standardCode: String? = "",

    // 14. 원료성분
    @ExcelProperty(index = 14)
    var rawIngredients: String? = "",

    // 15. 효능효과 (URL 포함)
    @ExcelProperty(index = 15)
    var efficacyUrl: String? = "",

    // 16. 용법용량 (URL 포함)
    @ExcelProperty(index = 16)
    var dosageUrl: String? = "",

    // 17. 주의사항 (URL 포함)
    @ExcelProperty(index = 17)
    var precautionsUrl: String? = "",

    // 18. 첨부문서
    @ExcelProperty(index = 18)
    var attachDoc: String? = "",

    // 19. 저장방법
    @ExcelProperty(index = 19)
    var storageMethod: String? = "",

    // 20. 유효기간
    @ExcelProperty(index = 20)
    var validPeriod: String? = "",

    // 21. 재심사대상
    @ExcelProperty(index = 21)
    var reexamTarget: String? = "",

    // 22. 재심사기간
    @ExcelProperty(index = 22)
    var reexamPeriod: String? = "",

    // 23. 포장단위
    @ExcelProperty(index = 23)
    var packingUnit: String? = "",

    // 24. 보험코드
    @ExcelProperty(index = 24)
    var insuranceCode: String? = "",

    // 25. 마약류분류
    @ExcelProperty(index = 25)
    var narcoticClass: String? = "",

    // 26. 완제원료구분
    @ExcelProperty(index = 26)
    var finishedRaw: String? = "",

    // 27. 신약여부
    @ExcelProperty(index = 27)
    var newDrugYn: String? = "",

    // 28. 업종구분
    @ExcelProperty(index = 28)
    var businessType: String? = "",

    // 29. 변경내용
    @ExcelProperty(index = 29)
    var changeDetails: String? = "",

    // 30. 총량
    @ExcelProperty(index = 30)
    var totalAmount: String? = "",

    // 31. 주성분명
    @ExcelProperty(index = 31)
    var mainIngredient: String? = "",

    // 32. 첨가제명
    @ExcelProperty(index = 32)
    var additives: String? = "",

    // 33. ATC코드
    @ExcelProperty(index = 33)
    var atcCode: String? = "",

    // 34. 사업자번호
    @ExcelProperty(index = 34)
    var bizRegNum: String? = "",

    // 35. 위탁제조업체
    @ExcelProperty(index = 35)
    var contractManufacturer: String? = ""
)