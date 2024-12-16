package com.shorturl.shorturl.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 11:51 오전
 */
class ShortUrlTest {
    private final URL ANY_ORI_URL = URI.create("https://example.com").toURL();
    private final String ANY_SHORT_URL_KEY = "shorturl";

    ShortUrlTest() throws MalformedURLException {
    }

    @Test
    @DisplayName("ShortUrl 생성 성공 테스트")
    void createShortUrlKey() {
        //given
        //when
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_URL_KEY);
        //then
        assertThat(shortUrl.getOriUrl()).isEqualTo(ANY_ORI_URL);
        assertThat(shortUrl.getShortUrlKey()).isEqualTo(ANY_SHORT_URL_KEY);
    }

    @Test
    @DisplayName("ShortUrl 최소 생성 시 requestCnt의 값은 0이다.")
    void createShortUrlKeyWithZeroRequestCnt() {
        //given
        //when
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_URL_KEY);
        //then
        assertThat(shortUrl.getRequestCnt()).isEqualTo(0L);
    }

    @Test
    @DisplayName("ShortUrl 생성 실패 테스트")
    void createShortUrlKeyFail() {
        assertThatThrownBy(() -> ShortUrl.create(null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ShortUrl.create(ANY_ORI_URL, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ShortUrl.create(null, ANY_SHORT_URL_KEY)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("ShortUrl 요청 시 requestCnt가 하나씩 증가한다." )
    void increaseRequestCnt() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_URL_KEY);
        //when
        for (int i = 0; i < 13; i++) {
            shortUrl.increaseRequestCnt();
        }
        //then
        assertThat(shortUrl.getRequestCnt()).isEqualTo(13L);
    }
}