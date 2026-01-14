### A. 💡 프로젝트명 : 실시간 의약품 정보 검색 및 트렌드 분석 

+ 시스템핵심 가치: 대용량 공공 데이터의 초고속 검색을 위해 MSA 아키텍처와 Redis 캐싱을 적용하여 구현.
+ 구현 방향:대용량 처리: Spring Batch를 이용한 대규모 엑셀 데이터의 효율적인 DB 적재.
+ 고성능 통신: 서비스 간 통신에 REST 대신 gRPC 적용.
+ 검색 최적화: Redis를 활용한 초성 기반 자동 완성 및 인기 검색어 구현.

---

### B. 🛠️ 기술 스택 (Tech Stack)

+ Backend (MSA): Spring Boot, Kotlin, Gradle Multi-Module
+ Database: PostgreSQL database 17.7
+ Messaging/Protocol: gRPC, HTTP/REST
+ Frontend(작업 예정): React, Nginx
+ Infrastructure: AWS Lightsail, Docker 
+ CI/CD: GitHub Actions

---

### C. 🗺️ 시스템 아키텍처 및 데이터 흐름

+ 서비스 간 통신 전략: Protobuf를 사용하여, 기존 API 통신의 JSON 성능 문제를 해결
+ 데이터 처리 흐름
  > 배치 흐름: **Data Ingestion Service**가 Spring Batch를 사용. 엑셀로된 공공 데이터를 매일 하루 마다 조회 하여, 기존에 저장된 데이터의 버전을 비교 체크 후 Update 처리

---

### D. 🚀 주요 기능 및 구현 포인트

+ 대용량 데이터 적재
  >ingestion-service (Spring Batch)청크 처리, Batch Insert를 통한 성능 최적화, 트랜잭션 관리.

+ 초고속 검색
  >search-service (Redis)Redis의 Sorted Set/Hash 자료구조를 활용한 밀리초 단위 초성 인덱스 검색 구현.

+ API 통신
  > api-gateway-service & search-service (gRPC)Protobuf 파일 정의, 서비스 간 IDL(Interface Definition Language) 기반의 통신 구현.