# Library-Management-System(L.M.S)

## Swagger 주소
- api 명세의 경우 swagger 주소에서 확인해 주세요
- http://54.180.77.6:8080/swagger-ui.html#/


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
<br>


## 3. 프로젝트 기능

- 프로젝트 전체 기능 사진
<img src="https://github.com/ForteEscape/library-management-system/assets/24915062/9dd452a8-89f8-4628-a5c2-e127dad1d731">



### 회원 측 기능
- 회원은 도서관에 방문하지 않아도 도서의 정보를 간략하게 조회할 수 있습니다.


- 회원은 개인 상세페이지에서 다음과 같은 정보들을 확인할 수 있습니다.
  - 자신의 개인 정보(이름, 도서 회원 코드)
  - 자신이 현재 대여한 도서 목록
    - 대여한 도서에 대한 대여 기간 연장 신청
  - 현재 자신의 대여 가능 상태
  - 자신이 도서관에 요청한 요청 목록
  - 이번 달 남은 요청 가능 횟수
  - 회원 자신의 패스워드 변경


- 회원은 한달에 5번 도서관에 운영 개선 요구 또는 신규 도서 반입 요청을 등록할 수 있습니다.
  - 횟수는 매 달 1일 초기화됩니다.

### 도서 조회 기능
- 도서를 다음과 같은 기준으로 검색이 가능합니다.
  - 도서의 이름
  - 도서의 저자
  - 도서 출판사

  
- 모든 도서를 다음과 같은 기준으로 정렬이 가능합니다.
  - 대여된 횟수
  - 평점


- 모든 도서를 다음과 같은 기준으로 필터링이 가능합니다.
  - 도서 분류 코드(001 ~ 100번 이내에 포함되는 도서만 조회하는 용도로 구현하려고 합니다.)



- 추천 도서 기능을 사용할 수 있습니다.
  - 추천 도서 기능은 대여된 횟수, 평점에 의해 최대 30개까지 정렬된 데이터를 반환하게 됩니다.


## 관리자 측 기능
- 관리자는 다음과 같은 기능을 수행할 수 있습니다.


- 도서 등록 기능
  - 관리자는 새로운 도서를 등록할 수 있습니다.
  - 도서 등록에 필요한 데이터는 도서의 이름, 저자, 출판사, 출판년도, 비치된 위치, 분류 코드가 필요합니다.


- 회원 등록
  - 회원 등록에는 회원의 이름, 주소, 생년월일이 필요하며 3개 요소가 모두 동일한 경우 회원 등록이 제한됩니다.
  - 회원 등록의 결과로 회원 코드와 초기 비밀번호가 반환됩니다.


- 회원에 대한 삭제 기능


- 대여 기한 연장 처리
  - 대여 기한 연장을 신청한 시점이 대여 만료 일자보다 이전이고, 이미 대여 기한 연장을 수행한 것이 아닐 경우, 1회에 한하여 대여 만료 일자를 7일 늘려줍니다.
  - 만약 대여 기한 연장을 신청한 회원의 상태가 "대여 제한" 상태인 경우, 대여 기한 연장 처리가 불가능합니다.


- 등록된 운영사항 개선 요청 및 신규 도서 반입 요청에 대한 처리
  - 관리자는 각 요청에 대해 처분을 결정할 수 있습니다.(요청 수락 / 요청 거절)
  - 관리자는 각 요청에 대해 처분을 결정할 때 반드시 게시글의 형태로 결과를 적어 등록해야 합니다. 수락의 경우 수락 확인을, 거절의 경우 거절 사유를 적어야 합니다.


- 회원 패스워드 초기화


- 연/월 별 통계 조회 기능을 제공하며 다음과 같은 통계 데이터를 조회할 수 있습니다.
  - 대여 수
  - 도서 손/망실 비율


- 도서 대여 기록 등록
  - 도서 대여 기록은 회원의 회원 코드와 책의 정보가 필요합니다.
  - 대여를 신청한 회원의 현재 진행 중인 도서 대여 수가 2개를 초과할 경우 대여가 제한됩니다.
  - 도서 대여 기록 등록의 결과로 대여 시작 일자와 대여 만료 일자가 반환됩니다.


- 도서 대여 반납 처리
  - 도서 반납 시점이 대여 만료 시점을 초과했을 경우 회원은 초과된 날짜만큼 대여가 제한됩니다.
 
## 4. ERD

- Entity 다이어그램
<img src="https://github.com/ForteEscape/Library-Management-System/assets/24915062/e62fd7b7-b168-49d6-a3cd-4d59bd28c970">


- E-R 다이어그램
<img src="https://github.com/ForteEscape/Library-Management-System/assets/24915062/0ce00eb3-28c8-4f4b-83d1-6a6477c86244">

- 0 or many로 표기된 부분은 0 or 1 or many의 의미입니다.


## 5. TroubleShooting
- README 파일에는 맞닥뜨린 문제에 대한 해결 유무와 방법에 대한 요약만 서술합니다. 상세한 내용은 별도의 디렉토리를 두려고 합니다.
