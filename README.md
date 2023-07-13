# Library-Management-System(L.M.S)

## 1. 프로젝트 주제 설명
- L.M.S는 1개 도서관을 운영하는데 필요한 기능들을 구현하여 웹 서비스로 제공하는 프로젝트입니다.
- 회원이 사용할 수 있는 기능과 관리자가 사용할 수 있는 기능이 분리되어 있습니다.


## 2. 프로젝트 사용 기술 스택
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white">
<img src="https://img.shields.io/badge/QueryDSL-003545?style=for-the-badge&logo=querydsl&logoColor=white">
<img src="https://img.shields.io/badge/mariaDB-003545?style=for-the-badge&logo=mariaDB&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">
<br>

- ElasticSearch 는 프로젝트 전체 구현 완료 이후 여유가 될 시 적용할 예정입니다.


## 3. 프로젝트 기능

- 프로젝트 전체 기능 사진
<img src="https://github.com/ForteEscape/library-management-system/assets/24915062/9dd452a8-89f8-4628-a5c2-e127dad1d731">



### 회원 측 기능
- 회원은 도서관에 방문하지 않아도 도서의 정보를 간략하게 조회할 수 있습니다.


- 회원은 개인 상세페이지에서 다음과 같은 정보들을 확인할 수 있습니다.
  - 자신의 개인 정보
  - 자신이 현재 대여한 도서 목록
    - 대여한 도서에 대한 연장 신청
  - 현재 자신의 대여 가능 상태
  - 자신이 도서관에 요청한 요청 목록
  - 이번 달 남은 요청 가능 횟수
  - 회원 자신의 패스워드 변경


- 회원은 한달에 5번 도서관에 운영 개선 요구 또는 신규 도서 반입 요청을 등록할 수 있습니다.

### 도서 조회 기능
- 도서를 다음과 같은 기준으로 검색이 가능합니다.
  - 도서의 이름
  - 도서의 저자
  - 도서 출판사

  
- 도서를 다음과 같은 기준으로 정렬이 가능합니다.
  - 대여된 횟수
  - 평점


- 도서를 다음과 같은 기준으로 필터링이 가능합니다.
  - 도서 분류 코드



- 추천 도서 기능을 사용할 수 있습니다.
  - 추천 도서 기능은 대여된 횟수, 평점에 의해 최대 30개까지 정렬된 데이터를 반환하게 됩니다.


## 관리자 측 기능
- 관리자는 다음과 같은 기능을 수행할 수 있습니다.


- 회원에 대한 삭제 기능


- 대출 기한 연장 처리


- 등록된 운영사항 개선 요청 및 신규 도서 반입 요청에 대한 처리
  - 요청 처리 시 이에 대한 코멘트 작성이 수반되도록 할 예정입니다.


- 회원 패스워드 초기화


- 연/월 별 통계 조회 기능을 제공하며 다음과 같은 통계 데이터를 조회할 수 있습니다.
  - 대여 수
  - 도서 손/망실 비율


- 도서 대여 기록 등록


- 도서 대여 반납 처리
 
## 4. ERD

- Entity 다이어그램
<img src="https://github.com/ForteEscape/Library-Management-System/assets/24915062/0e926999-87b5-4a4b-976f-04030ba6c860">


- E-R 다이어그램
<img src="https://github.com/ForteEscape/Library-Management-System/assets/24915062/7412493e-2e53-4af5-9f81-094fcec5f3fa">


## 5. TroubleShooting
- README 파일에는 맞닥뜨린 문제에 대한 해결 유무와 방법에 대한 요약만 서술합니다. 상세한 내용은 별도의 디렉토리를 두려고 합니다.
