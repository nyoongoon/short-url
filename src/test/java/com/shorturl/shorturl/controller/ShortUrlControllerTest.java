package com.shorturl.shorturl.controller;

import com.shorturl.shorturl.dto.response.OriUrlResponse;
import com.shorturl.shorturl.dto.response.ShortUrlResponse;
import com.shorturl.shorturl.dto.response.UrlInfoResponse;
import com.shorturl.shorturl.service.ShortUrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:40 오후
 */
@WebMvcTest
class ShortUrlControllerTest {
    private final String ANY_SHORT_URL_KEY = "shorturl";
    private final URL ANY_ORI_URL = URI.create("https://example.com").toURL();
    private final long ANY_REQUEST_CNT = 13L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortUrlService shortUrlService;

    ShortUrlControllerTest() throws MalformedURLException {
    }

    @Test
    @DisplayName("ShortUrl 생성 테스트")
    void createShortUrlKey() throws Exception {
        //given
        when(shortUrlService.transformShortUrl(any())).thenReturn(new ShortUrlResponse(ANY_SHORT_URL_KEY));
        String json = "{\"oriUrl\":\"https://example.com\"}";

        //expected
        mockMvc.perform(post("/short-url")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrlKey").value(ANY_SHORT_URL_KEY))
        ;
    }

    @Test
    @DisplayName("ShortUrl 실패 테스트 - url가 blank일 경우 않을 경우 예외가 발생한다.")
    void createShortUrlFailWithNullOrEmptyUrlKeyForm() throws Exception {
        //given
        String nullUrl = "{\"oriUrl\":null}";
        String emptyUrl = "{\"oriUrl\":\"\"}";
        String blankUrl = "{\"oriUrl\":\" \"}";
        //expected
        postShortUrlFail(nullUrl);
        postShortUrlFail(emptyUrl);
        postShortUrlFail(blankUrl);
    }

    @Test
    @DisplayName("ShortUrl 실패 테스트 - 잘못된 url 형식")
    void createShortUrlFailWithInvalidUrlKeyForm() throws Exception {
        //given
        String json = "{\"oriUrl\":\"h123ttps:123//exam12le.com\"}";
        //expected
        postShortUrlFail(json);
    }

    private void postShortUrlFail(String nullUrl) throws Exception {
        mockMvc.perform(post("/short-url")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(nullUrl))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("getOriUrl 요청 시 shortUrlKey로 OriUrlResponse를 조회힌다.")
    void getOriUrl() throws Exception {
        //given
        when(shortUrlService.requestOriUrl(any())).thenReturn(new OriUrlResponse(ANY_ORI_URL));
        //expected
        mockMvc.perform(get("/{shortUrlKey}", ANY_SHORT_URL_KEY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oriUrl").value(ANY_ORI_URL.toString()))
        ;
    }

    @Test
    @DisplayName("getOriUrl 요청 시 shortUrlKey가 blank이면 예외가 발생한다.")
    void getOriUrlFail() throws Exception {
        //given
        //expected
        mockMvc.perform(get("/{shortUrlKey}", " ")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("oriUrl로 UrlInfoResponse를 조회한다.")
    void getUrlInfoByOriUrl() throws Exception {
        //given
        UrlInfoResponse response = UrlInfoResponse.of(ANY_ORI_URL, ANY_SHORT_URL_KEY, ANY_REQUEST_CNT);
        when(shortUrlService.getUrlInfoByOriUrl(any())).thenReturn(response);
        //expected
        mockMvc.perform(get("/url-info/origin")
                        .param("oriUrl", ANY_ORI_URL.toString())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oriUrl").value(ANY_ORI_URL.toString()))
                .andExpect(jsonPath("$.shortUrl").value(ANY_SHORT_URL_KEY))
                .andExpect(jsonPath("$.requestCnt").value(ANY_REQUEST_CNT))
        ;
    }

    @Test
    @DisplayName("oriUrl로 UrlInfoResponse를 조회 시 oriUrl이 blank 이면 예외가 발생한다.")
    void getUrlInfoByOriUrlFail() throws Exception {
        //given
        UrlInfoResponse response = UrlInfoResponse.of(ANY_ORI_URL, ANY_SHORT_URL_KEY, ANY_REQUEST_CNT);
        when(shortUrlService.getUrlInfoByOriUrl(any())).thenReturn(response);
        String oriUrl = " ";
        //expected
        mockMvc.perform(get("/url-info/origin")
                        .param("oriUrl", oriUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @DisplayName("shortUrlKey로 UrlInfoResponse를 조회한다.")
    void getUrlInfoByShortUrlKey() throws Exception {
        //given
        UrlInfoResponse response = UrlInfoResponse.of(ANY_ORI_URL, ANY_SHORT_URL_KEY, ANY_REQUEST_CNT);
        when(shortUrlService.getUrlInfoByShortUrlKey(any())).thenReturn(response);
        //expected
        mockMvc.perform(get("/url-info/short")
                        .param("shortUrlKey", ANY_SHORT_URL_KEY)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oriUrl").value(ANY_ORI_URL.toString()))
                .andExpect(jsonPath("$.shortUrl").value(ANY_SHORT_URL_KEY))
                .andExpect(jsonPath("$.requestCnt").value(ANY_REQUEST_CNT))
        ;
    }

    @Test
    @DisplayName("shortUrlKey로 UrlInfoResponse를 조회 시 shortUrlKey이 blank이면 예외가 발생한다.")
    void getUrlInfoByShortUrlKeyFail() throws Exception {
        //given
        UrlInfoResponse response = UrlInfoResponse.of(ANY_ORI_URL, ANY_SHORT_URL_KEY, ANY_REQUEST_CNT);
        when(shortUrlService.getUrlInfoByShortUrlKey(any())).thenReturn(response);
        String shortUrlKey = " ";
        //expected
        mockMvc.perform(get("/url-info/short")
                        .param("shortUrlKey", shortUrlKey)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
}