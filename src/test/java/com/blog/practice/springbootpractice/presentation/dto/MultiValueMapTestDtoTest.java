package com.blog.practice.springbootpractice.presentation.dto;

import com.blog.practice.springbootpractice.infrastructure.MultiValueMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.blog.practice.springbootpractice.presentation.dto.MultiValueMapTestDto1.Status;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

import static com.blog.practice.springbootpractice.infrastructure.LocalDateTimeUtils.toStringDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class MultiValueMapTestDtoTest {

    MultiValueMap<String, String > result;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new Jackson2ObjectMapperBuilder()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .timeZone("Asia/Seoul")
                .build();
    }

    @AfterEach
    void after() {
        result = null;
    }

    @Test
    @DisplayName("String/long/boolean/LocalDate/enum 모두 변환된다")
    void test1() {
        //given
        String expectedName = "name";
        int expectedAmount = 10000;
        boolean exptectChecked = true;
        LocalDateTime expectedLocalDateTime = LocalDateTime.of(2022,10,25,11,12,15);
        Status expectedStatus = Status.SUCCESS;

        MultiValueMapTestDto1 dto = MultiValueMapTestDto1.builder()
                .name(expectedName)
                .amount(expectedAmount)
                .checked(exptectChecked)
                .localDateTime(expectedLocalDateTime)
                .status(expectedStatus)
                .build();

        //when
        result = MultiValueMapConverter.convert(objectMapper,dto);

        //then
        assertThat(result.size()).isEqualTo(5);
        assertValue("name",expectedName);
        assertValue("amount",String.valueOf(expectedAmount));
        assertValue("checked",String.valueOf(exptectChecked));
        assertValue("dateTime",toStringDateTime(expectedLocalDateTime));
        assertValue("status",expectedStatus.name());

    }


    @Test
    @DisplayName("@JsonProperty로 지정된 필드명으로 변환된다.")
    void test2() {
        //given
        String expectedName = "name";
        int expectedAmount = 10000;
        boolean exptectChecked = true;
        LocalDateTime expectedLocalDateTime = LocalDateTime.of(2022,10,25,11,12,15);
        MultiValueMapTestDto2.Status expectedStatus = MultiValueMapTestDto2.Status.SUCCESS;

        MultiValueMapTestDto2 dto = MultiValueMapTestDto2.builder()
                .name(expectedName)
                .amount(expectedAmount)
                .checked(exptectChecked)
                .localDateTime(expectedLocalDateTime)
                .status(expectedStatus)
                .build();

        //when
        result = MultiValueMapConverter.convert(objectMapper, dto);

        //then
        assertThat(result.size()).isEqualTo(5);
        assertValue("na",expectedName);
        assertValue("date_time",toStringDateTime(expectedLocalDateTime));
        assertValue("st",expectedStatus.name());

    }


    private void assertValue(String name, String expectedName) {
        assertThat(getValue(result,name)).isEqualTo(expectedName);
    }

    private String getValue(MultiValueMap<String, String> result, String name) {
        return result.get(name).get(0);
    }

}