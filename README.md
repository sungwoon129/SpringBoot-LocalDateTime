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

## 1. 날짜포맷의 파라미터를 Java LocalDateTime으로 받기 ##
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
`@JsonFormat`을 이용해보는 건 어떨까요?   
`@JsonFormat`은 `Jackson`라이브러리에 선언되어 있는 어노테이션으로 `spring-boot-starter-web`의존성에 포함되어 있기 때문에 따로 의존성을 추가해줄 필요가 없습니다.
`@JsonFormat`과 Jackson에 대한 이야기는 밑에서 다시 해보도록 하고 우선 적용시켜보겠습니다.

```java
@Getter
@RequiredArgsConstructor
public class LocalDateDto {

    private final String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime dateTime;
}
```
> 예제코드에서는 년-월-일 다음에 공백을 두는 형태의 패턴을 사용했지만, 일반적으로는 띄어쓰기보다    
>`2022-10-26T11:00:00`과 같은 패턴을 선호합니다.
> 띄어쓰기로 인해서 잘못된 값이 넘어올 수 있기 때문입니다. 대신 T를 그대로 패턴에서 사용할 수 없어서 다음과 같이 ''로 감싸서 표현합니다.   
> pattern = "yyyy-MM-dd'T'HH:mm:ss"

테스트를 다시 해보겠습니다.

![@ModelAttribute 테스트 이미지](/images/1-1.GET_JsonFormat_테스트실패.png)
   

여전히 실패합니다. 콘솔창에 있는 오류 내용도 동일합니다. 어떻게 해야 해결할 수 있을까요?   
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


다시 테스트를 수행해 보겠습니다.

![@ModelAttribute 테스트 이미지](/images/2.png)

테스트가 성공한 것을 확인할 수 있습니다.   
`@ModelAttribute`를 사용한 Dto 형태로 날짜 데이터를 받을 때는 `@DateTimeFormat`을 사용하면 `LocalDateTime`으로 받을 수 있다는 것을 확인했습니다.

#### @RequestParameter ####
   

URL 파라미터를 키(필드)별로 받는 `@RequestParameter`의 경우에는 어떨까요?
이번에도 요청을 처리하는 컨트롤러 메소드를 만들어 보겠습니다.
```java
@GetMapping("/requestParameter")
public String get(@RequestParam(name = "dateTime")LocalDateTime dateTime) {

    log.info("request parameter 요청 데이터 = {}", dateTime);

    return "mission complete";
}
```
우선, 어노테이션 없이 테스트를 해보겠습니다.
```java
@DisplayName("requestParameter의 LocalDate는 변환된다")
@Test
public void test2() throws Exception {
    //given
    String url = "/requestParameter?dateTime=2022-10-27 15:27:20";

    //when
    ResultActions resultActions = mvc.perform(get(url));

    //then
    resultActions.andExpect(status().isOk())
            .andExpect(content().string(containsString("mission complete")));
}
```

![@RequestParameter 테스트 성공 이미지](/images/4.png)

`@ModelAttribute` 때와 동일한 원인으로 실패합니다.   
그래서 이번에도 `@DateTime` 어노테이션을 적용시켜보겠습니다.

```java
@GetMapping("/requestParameter")
public String get(
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @RequestParam(name = "dateTime")LocalDateTime dateTime) {

    log.info("request parameter 요청 데이터 = {}", dateTime);

    return "mission complete";
}
```
![@RequestParameter 테스트 성공 이미지](/images/3.png)

테스트를 통과했습니다. `@RequestParameter`도 `@DateTimeFormt`을 통해서 날짜포맷의 String을 LocalDateTime으로 직렬화 할 수 있다는 것을 확인했습니다.   
지금까지 GET 요청에서 날짜포맷의 String을 Java의 LocalDateTime에 매핑시키는지 알아보았습니다.   
다음은 POST 요청에서 어떻게 받을 수 있는지 알아보겠습니다.

   

### 1-2. Request Body ###

스프링에서는 일반적으로 Post 요청을 처리할 때, JSON 형태로 Request Body 에 데이터를 전송합니다.   
그리고 요청을 처리하는 컨트롤러에서는 `@RequestBody`를 적용시켜서 데이터를 받아서 처리합니다.
날짜포맷의 String도 마찬가지입니다. 그래서 GET 요청과 마찬가지로 날짜포맷의 스트링을 처리하는 컨트롤러 메소드와 DTO를 만들어 보겠습니다.
```java
@PostMapping("/post")
public String post(@RequestBody LocalDateJsonDto localDateJsonDto) {
    log.info("post 요청 데이터 = {}",localDateJsonDto);
    return "post mission complete.";
}
```

```java
@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;
    private LocalDateTime dateTime;
    
}
```
이제 데이터를 잘 받을 수 있는지 확인하는 테스트 코드를 작성해보겠습니다.

```java
@DisplayName("post요청시 requestBody의 LocalDate는 변환된다")
@Test
public void test3() throws Exception {
    //given
    String url = "/post";

    //when
    ResultActions resultActions = mvc.perform(post(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"name\":\"swy\", \"dateTime\":\"2022-10-27 16:24:00\"}"));

    //then
    resultActions
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("post mission complete.")));

}
```
GET 요청과 마찬가지로 먼저 아무런 어노테이션 없이 테스트를 해보겠습니다.

![POST 요청 어노테이션 없이 LocalDateTime 변환실패](/images/5.POST_LocalDateTime_변환실패1.png)

실패하는군요. 하지만 GET 요청때와는 메시지가 조금 다릅니다. 
String타입의 데이터를 LocalDateTime으로 직렬화하지 못했다고 합니다. 어떻게하면 직렬화 할 수 있을까요?   
우선, 위에서 GET 요청을 처리한 것처럼 `@DateTimeFormat` 어노테이션을 적용해보겠습니다.
```java
@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    
}
```
테스트를 해보면!!
![POST 요청 DateTimeFormat LocalDateTime 변환실패](/images/6.POST_LocalDateTime_변환실패.png)

>제가 참조한 [기억보단 기록을](https://jojoldu.tistory.com/361) 블로그에서는 `@DateTimeFormat`을 사용해서 직렬화하는 테스트가 성공했습니다.    
>하지만 저는 실패해서 원인을 찾아보니 패턴의 차이가 있었습니다. 참조한 블로그에서는 사용한 날짜패턴이 yyyy-MM-dd'T'HH:mm:ss 였고, 제가   
>사용한 패턴은 yyyy-MM-dd HH:mm:ss 입니다. `@DateTimeFormat`은 JSON parsing에 개입하지 않습니다. 현재 개발환경의 SpringBoot 메시지컨버터 기준으로        
>별도의 어노테이션없이 필드만 선언하여도 포맷변환없이 **LocalDateTime 클래스의 기본 패턴인 yyyy-MM-dd'T'HH:mm:ss 패턴으로 데이터가 들어오는 경우에는      
>테스트를 성공**하지만, **JSON parsing이 필요한 다른 패턴으로 데이터가 넘어온 경우에는 테스트가 실패**하게 됩니다.

실패합니다...
`@RequestBody`에서는 다른 방법이 필요한 것 같습니다.
Spring에서는 메시지를 교환할 때, 메시지가 JSON 문자열인 경우 jackson이 개입해서 메시지를 파싱하고 Java 객체에 매핑하는 역할을 수행합니다.
그렇다면 **Jackson에서 지원하는 어노테이션**인 `@JsonFormat`을 사용해보는 건 어떨까요?

```java
@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime dateTime;
    
}
```
테스트를 해보면!!!
![POST요청 @JsonFormat으로 변환성공](/images/7.POST_JsonFormat_테스트성공.png)
드디어 성공했습니다!   
`@RequestBody`에서는 `@JsonFormat`을 적용해야 원하는 포맷으로 날짜를 다룰 수 있다는 것을 알 수 있습니다.   
여기까지 해서 중간정리를 해보면 다음과 같은 사실을 알 수 있습니다.
+ URL 파라미터로 전달하는 GET 방식의 요청에서 LocalDateTime의 직렬화는 `@DateTimeFormat`으로 할 수 있다.
+ 파라미터를 JSON 형태로 전달하는 POST 방식의 요청에서 LocalDateTime의 직렬화는 `@JsonFormat`으로 할 수 있다.
+ 단, 파라미터를 JSON 형태로 전달하는 POST 방식의 요청에서 전달하는 날짜포맷이 LocalDateTime의 기본형인 yyyy-MM-ddTHH:mm:ss 인 경우에는 `@DateTimeFormat`으로도 직렬화가 가능하다.

   
   
   

### 1-3. Response Body ###

이번에는 서버가 요청에 응답할 때는 어떨지에 대해서 알아보겠습니다.
스프링부트에서는 컨트롤러에 `@RestController` 혹은 컨트롤러 메소드에 `@ResponseBody`가 적용되어 있는 경우, DTO 형태의 자바 클래스를 리턴할 때, jackson이 역직렬화하여   
JSON 문자열로 응답합니다.   
리턴하는 클래스에 LocalDateTime 타입의 필드를 선언하여 어떻게 되는지 확인해보겠습니다.

```java
@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;
    
    private LocalDateTime dateTime;

    public LocalDateJsonDto(String name, LocalDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }
}
```
다음은 LocalDateJsonDto 클래스를 리턴할 컨트롤러 메소드입니다.

```java
@GetMapping("/response")
public LocalDateJsonDto response() {
    return new LocalDateJsonDto("swy",LocalDateTime.of(2022,10,27,23,11,12));
}
```

여태까지와 마찬가지로 이 메소드를 테스트할 테스트코드를 작성해보도록 하겠습니다.

```java
@DisplayName("LocalDateJsonDto의 LocalDateTime은 변환된다")
@Test
public void test4() throws Exception {
    //given
    String url = "/response";

    //when
    ResultActions resultActions = mvc.perform(get(url));

    //then
    resultActions.andExpect(status().isOk())
                    .andExpect(content().json("{\"name\": \"swy\", \"dateTime\": \"2022-10-27 23:11:12\"}"));
}
```
이번에도 어노테이션 없이 먼저 테스트 해보겠습니다.

![@ResponseBody 어노테이션 없이 테스트 실패](/images/8.RESPONSE_NO_ANNOTATION.png)

테스트가 실패했습니다. 하지만 역직렬화 과정에서 Exception이 발생하거나 오류가 발생하진 않았습니다. status도 200이고 body도 잘 들어가 있습니다.   
아무런 어노테이션이 없는 경우에는 LocalDateTime의 기본포맷인 yyyy-MM-ddTHH:mm:ss 형식으로 반환하는 것을 볼 수 있습니다.   
제가 원하는 포맷으로 리턴하려면 어떻게 해야할까요? 먼저 `@DateTimeFormat`을 적용시켜 보겠습니다.
```java
@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    public LocalDateJsonDto(String name, LocalDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }
}
```
테스트를 해보겠습니다.

![@ResponseBody @DateTimeFormat 실패](/images/9.RESPONSE_DateTimeFormat_테스트실패.png)

여전히 실패합니다. 실패원인도 어노테이션이 없을 때와 동일합니다.   
그렇다면, `@JsonFormat`을 적용시켜 보겠습니다.
```java
@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime dateTime;

    public LocalDateJsonDto(String name, LocalDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }
}
```
`@JsonFormat`의 결과는?

![@ResponseBody @JsonFormat 성공](/images/10.RESPONSE_JsonFormat_성공.png)

테스트를 통과합니다.   
정리하면, **`@ResponseBody`에서 원하는 포맷으로 리턴하려면 `@JsonFormat`을 사용**해야합니다.


### 2. [ @DateTimeFormat ]() vs [ @JsonFormat ]() ###

`@DateTimeFormat`과 `@JsonFormat`은 어떤 차이가 있을까요?   
왜 서로 동작하는 경우가 다른걸까요?   
스프링의 기본 JSON message converter는 Jackson입니다. Jackson은 스프링이 주고 받는 JSON 메시지들을 Java DTO 객체에 매핑해주고 변환하는 등의 다양한 처리를   
합니다. 그래서 Jackson에서 지원하는 어노테이션인 `@JsonFormat`은 LocalDate,LocalDateTime,YearMonth등을 JSON으로 직렬화할때 포맷을 관리합니다.

![라이브러리](/images/11.external_lib.png)

`spring-boot-starter-web`의존성 설정이 되어있다면, 별도로 설치하지 않아도 안에 포함되어 있습니다.   
Jackson 라이브러리가 포함되어 있다면 스프링은 JSON 직렬화를 Jackson을 통해서 진행합니다. Jackson이 없는 경우에는 기본으로 설정된 SpringBoot의 메시지컨버터에서   
JSON 직렬화를 처리합니다.(Spring Boot Message Converter에 대해서는 이 글에서 다루지 않겠습니다. 자세한 내용은 `MappingJacksonHttpMessageConverter`을 찾아보시면 좋을 것 같습니다.)
하지만 Jackson은 오직 JSON 데이터에 대해서만 동작하므로, URL 파라미터 요청에 대해서는 적용되지 않는 것입니다.
앞서 살펴보았던 `@RequestParameter` 혹은 `@ModelAttribute`는 `application/json` 형태의 데이터가 아니기 때문에 Jackson이 처리하지 않고 다른 메시지 컨버터가   
동작하므로, `@DateTimeFormat`이 날짜포맷의 문자열을 처리하게됩니다.
반면, JSON 데이터의 경우에는 개발자가 원하는 포맷으로 받거나 반환하려면 JSON 직렬화를 관리하는 Jackson의 어노테이션인 `@JsonFormat`을 활용해야합니다.   
 
   

### 3. 마무리 ###
마지막으로 정리해보겠습니다.

+ GET 요청시에는 `@DateTimeFormat`
+ POST 요청 + `@ResponseBody`에서 원하는 포맷으로 처리하고 싶다면 `@JsonFormat`
+ LocalDate,LocalDateTime의 기본포맷(ex : yyyy-MM-ddTHH:mm:ss)으로 처리한다면 어노테이션없이 필드만 선언   

이제 개발할 때, 더 이상 String으로 날짜를 받아서 LocalDateTime으로 변환해서 처리하는 일 없이 DTO 에서 바로 받을 수 있도록 합시다!
감사합니다.