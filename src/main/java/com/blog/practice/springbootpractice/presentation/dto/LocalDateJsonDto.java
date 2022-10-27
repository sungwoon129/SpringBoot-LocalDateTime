package com.blog.practice.springbootpractice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
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
