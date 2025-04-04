# 순수웨딩 2.0

![순수웨딩 소개](https://github.com/Step3-kakao-tech-campus/Team5_BE/assets/84652886/ebb9b772-69cd-413d-9d17-cb10fefdf714)

### 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [개발 주안점](#%EF%B8%8F개발-주안점)
3. [기술 스택](#-기술-스택)
4. [운영 인프라 환경](#%EF%B8%8F-운영-인프라-환경)
5. [프로젝트 구조 및 아키텍처](#-프로젝트-구조-및-아키텍처)
6. [기능 시연](#-기능-시연)

<br>

## 🚀 프로젝트 소개

**순수웨딩 2.0**은 예비 부부와 웨딩 플래너를 **투명한 가격과 실시간 상담을 통해 연결**하는 웨딩 매칭 플랫폼입니다.

이 프로젝트는 기존 **순수웨딩 1.0 팀 프로젝트**를 기반으로,  
**기획부터 프론트엔드, 백엔드, 인프라까지 모든 영역을 1인 풀스택으로 리팩토링 및 재구현**한 개인 프로젝트입니다.

<br>

### 🎯 Why 순수웨딩?

결혼 준비를 시작하면 누구나 느끼는 불편함이 있습니다.

- 💸 **불투명한 가격 정보**: 지인 소개로만 저렴한 견적을 받을 수 있는 구조
- 📑 **복잡한 정보 탐색 과정**: 웨딩 박람회, 블로그, SNS를 모두 뒤져야 하는 번거로움
- 🤝 **판매자 중심 시장**: 소비자가 아닌 판매자가 주도하는 구조
- 💼 **웨딩 플래너의 열정페이**: 경력 중심 수익 구조와 낮은 초봉

**👉 순수웨딩은 가격 공개, 실시간 상담, 리뷰 중심 매칭 기능을 통해 소비자가 주도하는 새로운 결혼 준비 경험을 제공합니다.**

<br>

### 💡 주요 기능

- 🤵🏻‍♀️ **플래너 포트폴리오 열람**

    - 플래너 자기소개, 예상 견적, 시공 사진, 리뷰 등 확인
    - 멤버십 가입 시 실제 계약 이력(업체명, 가격, 날짜 등) 열람 가능

- 💬 **실시간 채팅 상담**

    - 원하는 플래너와 1:1 채팅 상담
    - 견적 협의 및 계약 여부는 자유롭게 결정

- 🧾 **견적서 공유**

    - 채팅방 내 견적서 작성 및 수정 기능
    - 업체명, 가격, 진행 상태 등을 명확히 공유
    - 견적 확정 후 리뷰 작성 가능

- ⭐ **리뷰 및 찜하기**

    - 계약 완료 후 작성된 실제 후기 열람
    - 플래너 찜 기능으로 관심 플래너 저장

<br>

### 🗓 개발 기간

2025.03 ~ (진행 중)

<br>

### 🔗 링크 모음

- **서비스 배포 주소**: [https://sunsu-wedding.shop](https://sunsu-wedding.shop/)
- **프로젝트 문서(Notion)**: [순수웨딩 2.0 프로젝트 문서 바로가기](https://kimchanho.notion.site/2-0-1a0a1b1b0041809f8f31fa9314b10a34)

> **현재 서비스는 배포되어 접속 가능한 상태이며,**  
> **프로젝트의 모든 상세 내용은 아래 문서에서 확인할 수 있습니다.**

<br>

## 🛠️개발 주안점

### 📌 GitHub Actions 기반 CI/CD 파이프라인 구축

빌드/배포 자동화를 위해, **서버에 부담을 주지 않고 재현성과 효율성을 모두 갖춘 경량 CI/CD 파이프라인**을 설계했습니다.

- GitHub Actions에서 **Docker 이미지 빌드 → Docker Hub Push**
- EC2는 최신 이미지를 Pull만 하여 **빌드 과정 없이 배포**
- `deploy.sh`, `docker-compose.yml`, `.env` 자동 전송 및 실행
- GitHub Secrets를 통해 **민감 정보 안전하게 관리**
- 모든 과정은 스크립트 기반으로 자동화되어 **반복 배포에도 완전한 재현성 확보**

📂 [CI/CD 구축 상세 보기(Notion)](https://kimchanho.notion.site/GitHub-Actions-CI-CD-1c8a1b1b004180f586d4f8026b0dccb2?pvs=4)

<br>

### 📌 외부 결제 API를 추상화하여 테스트 가능한 구조로 개선

외부 결제 API(Toss Payments) 호출을 **비즈니스 로직에서 분리**하고,  
**DI 기반 인터페이스로 추상화**하여 테스트 가능성과 구조 유연성을 확보했습니다.

- `WebClient` 직접 호출 → **별도 구현체 분리(`TossPaymentApprovalClient`)**
- 서비스에서는 `PaymentApprovalClient` 인터페이스에만 의존
- 테스트 시 구현체를 Mock으로 대체 가능 → **안정적인 단위 테스트 가능**
- 결제 로직 확장 시(PayPal, Stripe 등) 구현체만 교체하면 됨 → **OCP 만족**

결과적으로 **SRP, OCP, IoC, DI 원칙을 적용하여 유지보수와 테스트가 용이한 결제 아키텍처로 개선**하였습니다.

📂 [외부 API 추상화 리팩토링 상세 보기(Notion)](https://kimchanho.notion.site/Spring-DI-API-1c8a1b1b004180cbaf07d587477678d3)

<br>

### 📌 결제 시스템 리팩토링: 무결성과 멱등성을 보장하는 아키텍처 설계

프론트 주도의 결제 흐름에서 발생하던 **위변조 위험성과 트랜잭션 불일치 문제**를 해결하기 위해,  
결제 승인과 유저 업그레이드를 **백엔드 트랜잭션 내에서 원자적으로 처리하는 구조**로 리팩토링했습니다.

- 결제 요청(`orderId`, `amount`)을 **사전에 서버에 저장**
- 결제 승인 시, **요청값과 저장값 일치 여부를 서버에서 검증**
- Toss 승인 요청과 유저 등급 업그레이드를 **하나의 트랜잭션으로 처리**
- 전체 로직에 대해 **정합성, 멱등성, 위변조 방지 요건을 만족**

이 구조를 통해 **보안적 신뢰성과 비즈니스 일관성을 모두 확보한 결제 시스템**을 완성했습니다.

📂 [결제 시스템 리팩토링 상세 보기(Notion)](https://kimchanho.notion.site/1c7a1b1b004180f1b65ffaaadd572f4a)

<br>

### 📌 포트폴리오 목록 조회 API - 성능 최적화

가장 많은 트래픽이 집중될 것으로 예상되는 **포트폴리오 목록 조회 API의 병목 구간을 식별하고,  
DTO Projection, 커서 기반 페이지네이션, Redis 캐싱, Connection/Thread Pool 튜닝을 적용하여 처리 성능을 대폭 개선**하였습니다.

- `v1(엔티티 조회 + 오프셋 페이징)` → `v4(DTO + 커서 페이징 + Redis)`로 개선
- **Max Throughput: 626 → 1,500 TPS (2.4배 상승)**  
  → *ramp-up 방식으로 테스트하며, 서버가 안정적으로 감당 가능한 최대 처리량 측정*
- **CPU 사용률: 87% → 57%**, **평균 레이턴시: 15ms → 8ms**  
  → *805 TPS 고정 조건에서의 성능 비교 (constant TPS 시나리오)*

📂 [테스트 환경 및 트래픽 시나리오 설계 보기(Notion)](https://kimchanho.notion.site/API-1c8a1b1b0041809ba00cd1f263ee6a8a)  
📂 [최종 성능 목표 달성까지의 튜닝 히스토리 보기(Notion)](https://kimchanho.notion.site/API-1c7a1b1b004180e5a6a9e7b61ceec67e)

<br>

### 📌 커버링 인덱스로 범용 쿼리 성능 3배 향상시키기

찜한 포트폴리오 ID 목록 조회는 포트폴리오 목록/찜 목록 API에서 **페이지네이션 요청마다 반복 호출되는 범용 쿼리**입니다.    
병목은 아니지만, 누적 호출 비용이 높아질 수 있어 **커버링 인덱스를 적용한 쿼리 최적화**를 수행했습니다.

- `IN 절 + CaseBuilder` 구조 유지, 불필요한 조인 없이 **구현 복잡도 최소화**
- `(user_id, portfolio_id)` 복합 인덱스로 **디스크 접근 없는 커버링 인덱스 구성**
- **평균 쿼리 실행 시간: 2.215ms → 0.775ms (약 65% 향상)**  
  → *실제 50만 건 기준, nanoTime 기반 테스트 결과*

조인 최적화 대신, **사전 최적화 관점에서 인덱스로 성능 확보** 후 병목 발생 시 구조 개선(JOIN)까지 확장 가능한 전략을 선택했습니다.

📂 [쿼리 구조와 커버링 인덱스 적용 과정 보기(Notion)](https://kimchanho.notion.site/3-1caa1b1b00418006806fe5be70cf4bcc)

<br>

## 🧰 기술 스택

- **Language & Framework**: Java 21, Spring Boot 3.4.3
- **Persistence**: Spring Data JPA, QueryDSL, MySQL, Redis
- **Security**: Spring Security, Spring Session
- **Monitoring**: Spring Boot Actuator, Micrometer, Prometheus, Grafana
- **Infra & DevOps**: Docker, Docker Compose, Nginx
- **Deployment**: GitHub Actions, SSL(Let's Encrypt)
- **Test**: JUnit5

<br>

## ☁️ 운영 인프라 환경

순수웨딩 백엔드는 **AWS 기반의 실서버 환경에서 운영**되며,  
각 기능별로 **책임이 분리된 최소 구성 인프라**를 활용해 전체 시스템을 안정적으로 구성하였습니다.

| 구성 요소              | 인프라 유형                         | 설명                                         |
|--------------------|--------------------------------|--------------------------------------------|
| **WAS 서버**         | EC2 (`t2.micro`)               | Spring Boot 애플리케이션 구동, Redis & Nginx 운영    |
| **DB 서버**          | RDS (MySQL, `db.t4g.micro`)    | 프로덕션 데이터베이스 운영                             |
| **Object Storage** | S3                             | 사용자 이미지, 에셋 등 정적 파일 저장소                    |
| **정적 프론트 호스팅**     | S3 + CloudFront + ACM          | React 정적 파일을 S3에 배포하고, CloudFront로 전송 최적화  |
| **DNS 관리**         | Route 53                       | 백엔드/프론트 도메인 연결 및 SSL 인증 설정                 |
| **모니터링 서버**        | Azure VM (`Standard B2ats v2`) | Prometheus + Grafana 운영, 시스템 성능 모니터링 전용 서버 |

> **💡 전체 인프라는 AWS를 기반으로 구성하되,  
> 모니터링 서버는 비용 효율성과 리소스 분산을 고려해 Azure 환경에 별도 구축하였습니다.**

<br>

## 🗂 프로젝트 구조 및 아키텍처

```
├───📂common                    # 공통 유틸, 예외, 공통 응답 객체 등
│   ├───📂entity                # BaseEntity, BaseTimeEntity 등 공통 엔티티 정의
│   ├───📂exception             # 전역 예외 및 ErrorCode 관리
│   ├───📂response              # ApiResponse, ErrorResponse 등 공통 응답 포맷
│   └───📂util                  # 공통 유틸리티 클래스
├───📂domain                    # 도메인 중심 구조 (수직 계층 구성)
│   ├───📁auth                  
│   ├───📁chat                  
│   ├───📁favorite              
│   ├───📁payment               
│   ├───📁portfolio             
│   └───📁user                  
│       ├───📁controller        # API 진입 지점
│       ├───📁service           # 도메인 서비스 (유즈케이스 처리)
│       ├───📁repository        # DB 접근 로직
│       └───📁exception         # 도메인 전용 예외
└───📂infra                     # 외부 시스템 및 기술 인프라 구성
    ├───📂config                # Spring 설정 클래스 (Redis, S3, Security 등)
    ├───📂redis                 # Redis 설정 및 캐시 전략
    ├───📂security              # Security 필터 등 보안 설정
    └───📂storage               # AWS S3 등 외부 저장소 연동
```

### 🧭 아키텍처 설계 원칙

**✅ 1. 도메인 모델 패턴 기반 설계**

- 단순한 트랜잭션 스크립트 방식이 아닌, **도메인 객체 중심의 책임 분산 설계**

- **핵심 비즈니스 규칙과 상태 변경 로직은 엔티티 또는 도메인 서비스 내부에 위치**  
  → 서비스 계층은 흐름만 조율하고, 실제 책임은 **도메인 스스로 처리**하는 구조

- 이를 통해 도메인 객체 간 협력이 명확해지고, **응집도 높은 비즈니스 로직 구현이 가능**

**✅ 2. 수직형 레이어드 아키텍처 (Vertical Layered Architecture)**

- 전통적인 `controller/service/repository`를 수평으로 나누는 방식이 아니라,  
  **도메인 단위로 각 계층을 하나의 폴더에 모아 관리**

- 예를 들어 `portfolio` 도메인 안에 관련 `controller, service, repository, exception` 객체가 모두 포함됨

- 덕분에 **한 도메인 폴더만 보면 API 흐름부터 내부 로직까지 한눈에 파악 가능**  
  → 관심사를 도메인 단위로 분리해 **가독성과 유지보수성**을 높인 구조

<br>

## 🎥 기능 시연

|                                                             포트폴리오 탐색                                                              |                                                             검색 및 필터링                                                              |
|:---------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------:|
| <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/18e4e9cc-87ed-4053-bec3-fd25e48fda29"> | <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/a52c056e-9912-4062-b926-b1c64eb78eb9"> |
|                             • 플래너 소개, 포트폴리오 이미지, 리뷰 확인 <br> • 멤버십 가입 시 실제 계약 이력(가격, 업체 등) 열람 가능 <br>                              |                                              • 지역, 가격 등 조건 설정 <br> • 플래너 이름 검색 기능 지원                                              |

|                                                      메시지 & 이미지 전송                                                      |                                                             채팅 응답 기능                                                              |
|:----------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------:|
| <img width="380" src="https://github.com/kimchanho97/algorithm/assets/104095041/d3b0faf7-d20c-4e83-9d66-00d2c38253c6"> | <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/0863820b-a151-4551-8c01-c8478e3a49ad"> |
|                                         • 텍스트 및 이미지 전송 가능 <br> • 실시간 채팅 상담 제공                                          |                                           • 메시지 읽음 표시 <br> • 안 읽은 메시지 개수 표시로 사용자 편의성 강화                                           |

|                                                           포트폴리오 작성 / 수정                                                           |                                                            견적서 작성 / 수정                                                            |
|:---------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------:|
| <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/2474a94c-6a19-4e02-b047-500b80b307a6"> | <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/7c2c1e74-4bb1-4682-b26f-51fb07015f1a"> |
|                                    • 플래너 자기소개, 예상 가격 등 정보 등록 및 수정 <br> • 이미지 업로드 및 포트폴리오 삭제 가능                                    |                                     • 채팅 내 견적서 작성 및 수정 가능 <br> • 업체명, 가격, 진행 상태 등 명확하게 기록 가능                                      |

|                                                            리뷰 작성 / 수정                                                             |                                                               리뷰 조회                                                               |
|:---------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------:|
| <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/aed20cd4-a50d-4084-ba63-99c00e160de7"> | <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/4997bb5f-aa6e-47d5-a60c-aea1d166f75c"> |
|                                           • 별점 및 후기를 작성, 수정, 삭제 가능 <br> • 실제 계약 후 작성 가능                                           |                                                          • 플래너별 리뷰 확인 가능                                                          |

|                                                                결제                                                                 |                                                                찜하기                                                                |
|:---------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------:|
| <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/1a03508c-5e5a-43c1-a367-3a8a82f92dcb"> | <img width="380" src="https://github.com/Step3-kakao-tech-campus/Team5_FE/assets/104095041/452e1f91-7115-46f2-83de-7e0e007fce99"> |
|                                           • Toss Payments 연동 <br> • 결제 완료 시 유저 등급 업그레이드                                           |                                       • 관심 플래너를 찜하기 등록/해제 가능 <br> • 마이페이지에서 찜한 플래너 목록 조회 가능                                       |

<br>