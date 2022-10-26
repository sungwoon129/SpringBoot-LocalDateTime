package com.blog.practice.springbootpractice.infrastructure;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MultiValueMapConverter {

    public static MultiValueMap<String, String> convert(ObjectMapper objectMapper, Object dto) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            Map<String,String> map = objectMapper.convertValue(dto, new TypeReference<>() {});
            params.setAll(map);

            return params;
        } catch (Exception e) {
            log.error("Url Parameter 변환 중 오류가 발생했습니다. requestDto={}", dto, e);
            throw new IllegalArgumentException("Url parameter 변환 중 오류가 발생했습니다.");
        }

    }
}
