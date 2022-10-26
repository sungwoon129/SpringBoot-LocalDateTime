package com.blog.practice.springbootpractice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MultiValueMapTestDto1 {

    private String name;
    private long amount;
    private boolean checked;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/seoul")
    private LocalDateTime dateTime;

    private Status status;

    @Builder
    public MultiValueMapTestDto1(String name, long amount, boolean checked, LocalDateTime localDateTime, Status status) {
        this.name = name;
        this.amount = amount;
        this.checked = checked;
        this.dateTime = localDateTime;
        this.status = status;
    }

    public enum  Status {
        SUCCESS, FAIL;
    }

}



