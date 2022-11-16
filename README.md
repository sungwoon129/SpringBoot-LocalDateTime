 SpringBoot JSON 형태의 날짜타입 LocalDateTime 으로 받기 
==============================================
오늘은 [ 기억보단 기록을 ](https://jojoldu.tistory.com/361) 블로그를 참조하여 공부한(이라 쓰고 '따라한'으로 읽는) 내용을 정리하려고 합니다.
작성한 코드는 모두 [ Github ](https://github.com/sungwoon129/SpringBootPractice) 에 있습니다.

## 0. 시작하며 ##

Spring을 사용해서 API 서버 혹은 웹 프로젝트를 개발하면서 Controller에서 요청을 받거나 응답을 주는 DTO에서 날짜 혹은 시간과 관련된 정보를 String 으로    
선언한 필드로 받아서 LocalDateTime으로 변환하는 것을 많이 보았고, 얼마 전까지 저 또한 그렇게 처리했습니다. 그렇게 변환하면서도 비효율적이라고 많이 생각하고    
고민하며 찾아보다가 좋은 방법을 찾아서 공유하려고 합니다.

저는 다음과 같은 개발환경에서 진행하였습니다.

    - SpringBoot 2.7.5
    - Java 11
    - Gradle7
    - spring-boot-starter-web 2.7.5
    - Lombok 1.18.24
    - spring-boot-starter-test 2.7.5

혹시 Spring Boot 1.X 버전을 사용하시는 분들은 [ JSR 310 ](https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310/2.9.7) 이 필요하다고 합니다. 
없으면 `json parse error`가 발생한다고 합니다.   
Spring Boot 2.X 버전부터는 JSR 310이 기본 의존성으로 포함되어 있어 따로 의존성을 추가하지 않으셔도 됩니다.

## 1. 요청/응답 방법별 LocalDateTime 변환 방법 ##
뷰 혹은 클라이언트에서 서버로 GET이나 POST같은 HTTP 메소드를 사용해서 데이터를 보냅니다.    
GET 메소드에서는 주로 URL 파라미터를 통해서 쿼리스트링형태로 보내고, POST 메소드에서는 Request Body 에 JSON 형태로 보내는 방법을 자주 사용합니다.   
지금부터 유형별로 확인해 보겠습니다.

### 1-1. URL Parameter ###
   
Spring 에서 URL 파라미터을 받는 방법은 크게 2가지가 있습니다.

+ @ModelAttribute를 사용해서 DTO 객체로 받기
+ @RequestParameter를 사용해서 필드별로 받기

각각의 방식별로 LocalDateTime으로 변환하는 방법을 알아보겠습니다.

#### @ModelAttribute ####
먼저, 요청을 처리할 컨트롤러를 생성하겠습니다.
```java
@GetMapping("/get")
public String get(LocalDateDto dto) {
        return "mission complete";
}
```
>별도의 어노테이션을 지정하지 않으면 기본적으로 스프링은 `@ModelAttribute`를 할당합니다   

HTTP GET 으로 `/get` 이라는 url로 요청을 보내면 url 파라미터의 키들이 LocalDateDto의 픨드에 매핑될 것입니다.   
LocalDateDto는 name과 dateTime만 받는 간단한 내용입니다.   

```java
@Getter
@RequiredArgsConstructor
public class LocalDateDto {
    private final String name;
    private final LocalDateTime dateTime;
}
```
우선, 별다른 일을 하지 않고 이 상태로 테스트를 진행해보겠습니다.

```java
@WebMvcTest
@ExtendWith(SpringExtension.class)
class LocalDateControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void ModelAttribute의_LocalDate는_변환된다() throws Exception {
        //given
        String url = "/get?name=swy&dateTime=2022-10-27 15:27:20";

        //when
        ResultActions resultActions = mvc.perform(get(url));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("mission complete"));
    }
}
```
GET 요청을 보내는 간단한 테스트입니다. 이제 실행해 보겠습니다.

![@ModelAttribute 테스트 이미지](/images/1.png)

테스트가 실패했습니다.   
dateTime 필드의 변환에 실패했다고 합니다. 쿼리스트링의 dateTime 키의 값은 String 타입인데, LocalDateDto의 dateTime 필드의 타입은 LocalDateTime이라서 변환할 수 없다고 합니다.   
이 문제를 해결하기 위해서 `@DateTimeFormat`이라는 **스프링에서 지원하는 어노테이션**이 있습니다.   
이 어노테이션은 `LocalDate`와`LocalDateTime`같은 날짜관련타입의 직렬화 및 변환을 지원합니다.   
LocalDateDto의 dateTime필드에 어노테이션을 붙여주겠습니다. 
```java
@Getter
@RequiredArgsConstructor
public class LocalDateDto {

    private final String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime dateTime;
}
```
> 예제코드에서는 년-월-일 다음에 공백을 두는 형태의 패턴을 사용했지만, 일반적으로는 띄어쓰기보다    
>`2022-10-26T11:00:00`과 같은 패턴을 선호합니다.
> 띄어쓰기로 인해서 잘못된 값이 넘어올 수 있기 때문입니다. 대신 T를 그대로 패턴에서 사용할 수 없어서 다음과 같이 ''로 감싸서 표현합니다.   
> pattern = "yyyy-MM-dd'T'HH:mm:ss"

다시 테스트를 수행해 보겠습니다.

![@ModelAttribute 테스트 이미지](/images/2.png)

테스트가 성공한 것을 확인할 수 있습니다.   
`@ModelAttribute`를 사용한 Dto 형태로 날짜 데이터를 받을 때는 `@DateTimeFormat`을 사용하면 `LocalDateTime`으로 받을 수 있다는 것을 확인했습니다.

### 1-2. Request Body ###

### 1-3. Response Body ###