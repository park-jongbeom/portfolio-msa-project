### A. 💡 프로젝트명 : 실시간 의약품 정보 검색 및 트렌드 분석 

+ 시스템핵심 가치: 대용량 공공 데이터의 초고속 검색을 위해 MSA 아키텍처와 Redis 캐싱을 적용하여 구현.
+ 구현 방향:대용량 처리: Spring Batch를 이용한 대규모 엑셀 데이터의 효율적인 DB 적재.
+ 고성능 통신: 서비스 간 통신에 REST 대신 gRPC 적용.
+ 검색 최적화: Redis를 활용한 초성 기반 자동 완성 및 인기 검색어 구현.

---

### B. 🛠️ 기술 스택 (Tech Stack)

+ Backend (MSA): Spring Boot, Kotlin, Gradle Multi-Module
+ Database: MySQL 8.0, Redis
+ Messaging/Protocol: gRPC, HTTP/REST
+ Frontend: React, TypeScript (선택), Vite

---

### C. 🗺️ 시스템 아키텍처 및 데이터 흐름

여기에 기술적 선택의 이유와 함께 아키텍처 다이어그램을 삽입합니다.

+ MSA 구조 설명: 왜 모놀리식이 아닌 MSA를 선택했는지 (예: 서비스별 독립적 확장, 역할 분리).
+ 서비스 간 통신 전략:gRPC 선택 이유: REST 대비 성능 우위와 Protobuf를 통한 스키마 일관성을 강조.
+ 데이터 처리 흐름 (Flow Diagram):배치 흐름: **Data Ingestion Service**가 Spring Batch를 이용해 엑셀을 MySQL과 Redis에 적재하는 과정 설명.
+ 검색 흐름: 사용자 요청 → API Gateway → gRPC → Search Service → Redis/MySQL 조회 과정을 단계별로 설명.

---

### D. 🚀 주요 기능 및 구현 포인트

단순한 기능 리스트가 아닌, 기술적 관점에서 난이도가 높았던 부분을 강조합니다.

기능구현 기술 및 목표5년 차 강조 포인트

+ 대용량 데이터 적재
>ingestion-service (Spring Batch)청크 처리, Batch Insert를 통한 성능 최적화, 트랜잭션 관리.

+ 초고속 검색
>search-service (Redis)Redis의 Sorted Set/Hash 자료구조를 활용한 밀리초 단위 초성 인덱스 검색 구현.


+ API 통신
> api-gateway-service & search-service (gRPC)Protobuf 파일 정의, 서비스 간 IDL(Interface Definition Language) 기반의 통신 구현.