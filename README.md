# BE-Repository 🚗

### 📅 프로젝트 일정 
2023년 07월 28일 ~ 09월 08일 (6주)

### 백엔드 아키텍쳐
<img src='./src/main/resources/image/아키텍처.jpeg' width="100%">

### 사용 라이브러리
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=Spring-Boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring WebSocket](https://img.shields.io/badge/Spring_WebSocket-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)](https://spring.io/guides/gs/messaging-stomp-websocket/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=Thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://swagger.io/)
[![QueryDSL](https://img.shields.io/badge/QueryDSL-FFA500?style=for-the-badge)](http://www.querydsl.com/)
[![JSON](https://img.shields.io/badge/JSON-000000?style=for-the-badge&logo=JSON&logoColor=white)](https://www.json.org/json-en.html)
[![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=Amazon-S3&logoColor=white)](https://aws.amazon.com/s3/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white)](https://redis.io/)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON-Web-Tokens&logoColor=white)](https://jwt.io/)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)](https://www.mysql.com/)
[![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge)](https://projectlombok.org/)
[![Spring Test](https://img.shields.io/badge/Spring_Test-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)](https://spring.io/guides/gs/testing-web/)
[![JavaMailSender](https://img.shields.io/badge/JavaMailSender-0078D4?style=for-the-badge)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSender.html)
[![Socket.io](https://img.shields.io/badge/Socket.io-010101?style=for-the-badge&logo=Socket.io&logoColor=white)](https://socket.io/)
[![WebRTC](https://img.shields.io/badge/WebRTC-333333?style=for-the-badge&logo=WebRTC&logoColor=white)](https://webrtc.org/)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)](https://gradle.org/)

### 기술적 의사 결정
| 기술 | 이유|
|----|----------------------------------------------------------------------------------------------|
| Java | 플랫폼 독립성, 안정성, 객체 지향의 특징을 활용하여 다양한 시스템과 장치에서 안정적으로 작동하는 애플리케이션을 구축할 수 있기 때문에 채택                                                                                                                                                                                                                                                                                              |
| Spring | 객체 지향 특성을 기반으로, 스프링 프레임워크의 유연성과 강력한 생산성 향상 도구를 활용하여 안정적이고 확장 가능한 백엔드 애플리케이션을 효과적으로 개발                                                                                                                                                                                                                                                                                       |
| Spring Security | 스프링과의 연계성을 토대로 로그인, 인증, 인가 처리와 관련하여 filter로 처리가능한 기능을 제공하고 cors처리까지 처리할 수 있도록 제공 하여 사용                                                                                                                                                                                                                                                                                      |
| MySQL | 명확한 스키마와 데이터 무결성, 여러 데이터의 관계성을 고려하여 rdbms 중 가장 보편적이고 많은 자료를 찾을 수 있음                                                                                                                                                                                                                                                                                              |
| Redis | Refresh Token 사용을 위해 간단한 정보를 기존 MySQL보다 빠르게 읽고 쓰기를 할 수 있도록 메모리 저장, key-value 형태의 저장소인 Redis를 채택, 추가로 만료 시간을 설정하여 데이터량을 관리 가능                                                                                                                                                                                                                                                   |
| EC2 | 비용 효율성을 갖춘 클라우드 환경에서 신속하게 가상 서버를 관리할 수 있어, 백엔드 서버의 배포와 운영을 유연하게 처리                                                                                                                                                                                                                                                                                             |
| S3 | 데이터에 대한 세밀한 액세스 제어가 가능하며 비용효율이 좋음                                                                                                                                                                                                                                                                                                                                    |
| Refresh Token | 보안을 위해 기존의 Access Token의 만료 시간을 짧게 설정하면서 사용자가 불편함을 느끼지 않도록 Access Token을 갱신받을 수 있도록 도입. 또한, 사용자 편의성과 보안 측면을 고려하여 동일한 계정으로 동시 접속 가능한 수를 제한하며 추가 접속 요청 시 기존 계정 중 랜덤 중 하나의 Refresh Token 값을 삭제하여 로그아웃 처리하며 하나의 Refresh Token으로 두 명의 사용자가 Access Token을 재발급 요청한 경우, 어느 쪽이 Refresh Token을 탈취한 쪽인지 판별할 수 없으므로 해당 Refresh Token을 삭제하여 탈취된 Refresh Token을 사용할 수 없도록 처리                                                                                                                                                                                                                                                                            |
| Nginx | letsencrypt를 통한 HTTPS 설정과 Reverse Proxy 기능을 위해 도입, 이후에 서버가 늘어나거나 할 경우 Nginx 통해 로드밸런싱도 가능, AWS도 로드밸런서와 HTTPS 인증서를 발급 받을 수 있지만, AWS 이외의 서비스를 사용하는 경우에도 적용할 수 있는 방법을 채택하는 것이 더 좋은 방법이라고 생각                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| QueryDSL | Spring에서 제공하는 JPA도 간단한 쿼리를 요청할 경우에는 좋았지만, DB내에 존재하는 값의 통계를 내기 위해서 SQL과 유사한 쿼리를 작성할 수 있는 JPQL을 도입 하지만, 많은기능 제공과 compilet시점에서 에러를 발견할 수 있어 사용                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| Socket.io | 실시간 양방향 통신을 위해 사용. 웹소켓 프로토콜을 기반으로 하며, 웹소켓이 지원되지 않는 환경에서도 폴링 방식으로 통신이 가능함. 다양한 환경과 브라우저에서 안정적인 실시간 통신을 구현 가능                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| WebRTC | 웹 브라우저 간에 피어 투 피어(P2P) 방식으로 직접적인 데이터, 비디오, 오디오 통신을 가능함. 별도의 플러그인이나 네이티브 앱 없이도 브라우저에서 직접 멀티미디어 통신을 할 수 있게 해주며, 낮은 지연시간과 고품질의 통신 제공                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| Oauth2 | 클릭 몇 번 만으로 로그인을 할 수 있는 편리성을 통해 사용자들이 우리 서비스를 더 찾을 수 있도록 채택                                                                                                                                                                                                                                                                                                        |
 
### 트러블 슈팅
<details>
  <summary> OpenAPI vs MySQL </summary>
<br> OpenAPI를 통해 데이터를 조회했으나 서버 상태에 따른 지연과 과도한 의존성으로 인해 비효율적이었습니다. 초기에는 코드 최적화를 시도하여 시간을 약간 줄였지만 본질적인 문제는 OpenAPI 서버에서 발생하는 것으로 확인되었습니다.<br>
 필요한 데이터만을 선별하여 DB에 저장하고 이를 조회하는 방식으로 변경함으로써 조회 시간을 크게 단축시킬 수 있었습니다.
</details>

<details>
  <summary> Refresh Token 탈취 판단 </summary>
<br> 기존의 Refresh Token 탈취 판단은 Access Token 발급상태를 통해 이루어졌습니다. 프론트 비동기요청에서 Access Token 재발급 상황이 여러번 발생, 여기서 탈취 되었다고 판단하여 Refresh Token 삭제가 이루어지는 경우가 발생했습니다.<br>
대체할 판단기준으로 발급시의 IP Address를 확인과 Refresh Token 매번 새로 발급하도록 하였습니다.
</details>

<details>
  <summary> "동등 연산자(==)"와 "equals" </summary>
  <br> Id 비교 과정에서 동일한 값을 가진 두 값을 비교했는데 '같지 않다'라는 결과가 나왔습니다. "동등 연산자(==)"는 대상의 주소를 비교하여 boolean 값을 반환하며, Id 값의 타입이 참조형 자료형인 Long으로 인해 서로 다른 주소값을 가지므로 false 값을 반환합니다.<br>
 값 자체를 비교하는 것이 목적이라 "equals" 메서드를 사용하였습니다.
</details>
