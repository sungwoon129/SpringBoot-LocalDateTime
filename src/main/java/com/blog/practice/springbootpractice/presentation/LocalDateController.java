package com.blog.practice.springbootpractice.presentation;

import com.blog.practice.springbootpractice.presentation.dto.LocalDateDto;
import com.blog.practice.springbootpractice.presentation.dto.LocalDateJsonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class LocalDateController {

    @GetMapping("/get")
    public String get(LocalDateDto dto) {
        return "mission complete";
    }

    @GetMapping("/requestParameter")
    public String get(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(name = "dateTime")LocalDateTime dateTime) {

        log.info("request parameter 요청 데이터 = {}", dateTime);

        return "mission complete";
    }

    @PostMapping("/post")
    public String post(@RequestBody LocalDateJsonDto localDateJsonDto) {
        log.info("post 요청 데이터 = {}",localDateJsonDto);
        return "post mission complete.";
    }

    @GetMapping("/response")
    public LocalDateJsonDto response() {
        return new LocalDateJsonDto("swy",LocalDateTime.of(2022,10,27,23,11,12));
    }

}
