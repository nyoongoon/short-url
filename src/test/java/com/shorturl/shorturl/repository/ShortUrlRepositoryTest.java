package com.shorturl.shorturl.repository;

import com.shorturl.shorturl.domain.ShortUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 1:15 오후
 */
@DataJpaTest
class ShortUrlRepositoryTest {
    private final URL ANY_ORI_URL = URI.create("https://example.com").toURL();
    private final String ANY_SHORT_URL_KEY = "shorturl";

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    ShortUrlRepositoryTest() throws MalformedURLException {
    }

    @Test
    @DisplayName("원본 Url로 ShortUrl 조회하기")
    void findByOriUrl() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_URL_KEY);
        ShortUrl save = shortUrlRepository.save(shortUrl);
        //when
        ShortUrl found = shortUrlRepository.findByOriUrl(ANY_ORI_URL).orElseThrow();
        //then
        assertThat(save).isEqualTo(found);
    }
    @Test
    @DisplayName("ShortUrlKey로 ShortUrl 조회하기")
    void findByShortUrlKey() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_URL_KEY);
        ShortUrl save = shortUrlRepository.save(shortUrl);
        //when
        ShortUrl found = shortUrlRepository.findByShortUrlKey(ANY_SHORT_URL_KEY).orElseThrow();
        //then
        assertThat(save).isEqualTo(found);
    }
}