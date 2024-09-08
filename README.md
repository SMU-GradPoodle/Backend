### 프로젝트 소개

학우들의 통학로 불편을 해소하기 위해 만든 실시간 시각화 지도입니다. 학우들의 주요 통학 대중교통편 중 광화문 버스, 1호선 지하철 등 잦은 시위로 인해 불편을 겪는 것을 인식하고 해결하고자 기획하였습니다.

### 사용 기술

`JAVA` `Spring Boot` `JPA` `MySQL` `Redis` `Docker` `AWS` `Github Actions` `nginx`

### 주요 기능

- 학생 메일 인증을 통한 회원가입 및 로그인 기능
- 길찾기 API를 호출하여 서버 DB에 버스, 지하철, 정류장, 경로 등의 정보를 저장하는 관리자용 스크래핑 기능
- 실시간 버스 위치 및 도착 정보 API를 호출하여 캐싱하는 스케줄러
- 서비스에서 제공하는 특정 지점에서 학교로 통학하는 경로 추천 및 실시간 버스 위치 시각화 기능
- 서비스에서 알 수 없는 교통 이슈를 제보하는 커뮤니티 기능

### 구현 화면

**경로 검색 기능**

<img width="706" alt="1" src="https://github.com/user-attachments/assets/6e2acc62-fc79-4f5c-ab92-2b50e71033dd">



**실시간 교통 상황 조회 기능**

<img width="706" alt="2" src="https://github.com/user-attachments/assets/fdd18d06-76ec-4d58-9333-ed0620517292">


### 아키텍처 구성도
<img width="716" alt="3" src="https://github.com/user-attachments/assets/23b5d124-a6a5-488b-90c9-2a912c4a8205">
