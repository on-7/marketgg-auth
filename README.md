# Market GG Auth Server

해당 프로젝트는 Market GG 에 회원가입, 인증 API 를 제공함으로써 Client 의 로그인, 회원가입 요청을 수행하여 회원정보를 저장, JWT 발급을 수행합니다.

# Getting Started

```
./mvnw spring-boot:run
```

# Project Architecture

![marketgg-architecture-v1-0-2](https://user-images.githubusercontent.com/38161720/183286626-494edf2f-2a6c-4207-b70f-57d85b838704.png)


# Features

## 회원가입

- 덕춘 작성

## 로그인

- 사용자가 email, password 정보로 로그인 요청 시 DB 의 값과 비교하여 적절한 요청의 경우 JWT 를 발급하여 인증을 수행한다.

### Techinical Issue

- [MSA 환경에서의 인증](https://github.com/nhn-on7/marketgg-auth/wiki/MSA-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C%EC%9D%98-%EC%9D%B8%EC%A6%9D)

## Tech Stack

### Build Tools

![ApacheMaven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=ApacheMaven&logoColor=white)

### Datebases

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white)

### DevOps

![NHN Cloud](https://img.shields.io/badge/-NHN%20Cloud-blue?style=flat&logo=iCloud&logoColor=white)
![GitHubActions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=GitHubActions&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-4E98CD?style=flat&logo=SonarQube&logoColor=white)

### Frameworks

![SpringBoot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=SpringBoot&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=SpringSecurity&logoColor=white)

### Languages

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=Java&logoColor=white&style=flat)

### Testing Tools

![Junit5](https://img.shields.io/badge/Junit5-25A162?style=flat&logo=Junit5&logoColor=white)

### 형상 관리 전략

![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=GitHub&logoColor=white)
![Sourcetree](https://img.shields.io/badge/Sourcetree-0052CC?style=flat&logo=Sourcetree&logoColor=white)

- Git Flow 를 사용하여 관리
  모든 브랜치는 Pull Request에 코드 리뷰 진행 후 merge 합니다.
  ![image](https://user-images.githubusercontent.com/71637466/183255360-68cb4eef-cbc3-4005-9889-bf8bed192b43.png)
- Main: 배포시 사용
- Develop: 개발 단계가 끝난 부분에 대해서 Merge 내용 포함
- Feature: 기능 개발 단계
- Hot-Fix: Merge 후 발생한 버그 및 수정사항 반영 시 사용

## ERD

![marketgg_auth-v2.2.2](https://user-images.githubusercontent.com/38150034/183289775-d2b299c0-6bd9-493c-86e5-5e9632e38d8b.png)

## Contributors

<a href="https://github.com/nhn-on7/marketgg-auth/graphs/contributors">
<img src="https://contrib.rocks/image?repo=nhn-on7/marketgg-auth" />
</a>


## License

Market GG is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
