package com.blog.practice.springbootpractice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class LocalDateJsonDto {

    private String name;

    /**
     * @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     * 클라이언트에서 Json 데이터를 post 요청으로 requestBody 안에 위의 포맷으로 보내도 Json 데이터를 직렬화 하는 라이브러리는 Jackson 이므로
     * 아래 어노테이션은 동작하지 않습니다. 왜냐하면 jackson은 직렬화할때 @DateTimeFormat이 무엇인지 모르기 때문입니다.
     * @JsonFormat 어노테이션이 없다면 LocalDateTime의 기본 포맷인 yyyy-MM-ddTHH:mm:ss 만 받을 수 있습니다. @DateTimeFormat의 Json 데이터의 Dto 매핑에 영향을 주지 않습니다
     */
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime dateTime;

    public LocalDateJsonDto(String name, LocalDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }
}
