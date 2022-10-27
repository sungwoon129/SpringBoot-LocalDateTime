package com.blog.practice.springbootpractice.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

}