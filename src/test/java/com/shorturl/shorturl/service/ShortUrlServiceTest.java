package com.shorturl.shorturl.service;

import com.shorturl.shorturl.domain.ShortUrl;
import com.shorturl.shorturl.dto.request.ShortUrlRequest;
import com.shorturl.shorturl.dto.response.OriUrlResponse;
import com.shorturl.shorturl.dto.response.ShortUrlResponse;
import com.shorturl.shorturl.dto.response.UrlInfoResponse;
import com.shorturl.shorturl.repository.ShortUrlRepository;
import com.shorturl.shorturl.util.randomGenerator.RandomNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.DUPLICATED_KEY_RETRY;
import static com.shorturl.shorturl.util.constant.CharSet.BASE56_CHARSET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 2:35 오후
 */
@Transactional
@SpringBootTest
class ShortUrlServiceTest {
    private final URL ANY_ORI_URL = URI.create("https://example.com").toURL();
    private final String ANY_SHORT_KEY = "shortKey";

    @Autowired
    private ShortUrlService shortUrlService;
    @MockBean
    private RandomNumberGenerator randomNumberGenerator;
    @Autowired
    private ShortUrlRepository shortUrlRepository;

    ShortUrlServiceTest() throws MalformedURLException {
    }

    @BeforeEach
    void setUp() {
        when(randomNumberGenerator.nextInt(anyInt(), anyInt())).thenReturn(7);
        when(randomNumberGenerator.nextInts(anyInt(), anyInt())).thenReturn(List.of(1, 2, 3, 4, 5, 6, 7));
    }

    @Test
    @DisplayName("원본 Url로 shorUrl을 생성하여 ShortUrlResponse를 리턴한다.")
    void transformShortUrl() {
        //given
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        //when
        ShortUrlResponse response = shortUrlService.transformShortUrl(request);
        //then
        assertThat(response).isNotNull();
        assertThat(response.getShortUrlKey()).isNotNull();
    }

    @Test
    @DisplayName("원본 Url이 동일하면 동일한 ShortUrl를 반환한다. - oriUrl에 대한 ShortUrl이 이미 존재하면 그대로 리턴")
    void transformShortUrlBySameUrl() {
        //given
        ShortUrlRequest request1 = new ShortUrlRequest(ANY_ORI_URL);
        ShortUrlRequest request2 = new ShortUrlRequest(ANY_ORI_URL);
        //when
        ShortUrlResponse response1 = shortUrlService.transformShortUrl(request1);
        ShortUrlResponse response2 = shortUrlService.transformShortUrl(request2);
        //then
        assertThat(response1.getShortUrlKey()).isEqualTo(response2.getShortUrlKey());
    }

    @Test
    @DisplayName("shortUrlKey는 randomNumberGenerator에서 반환된 사이즈로 생성된다. - 4자 이상 8자 이하로 설정")
    void requestOriUrlByRandomNumberGenerator() {
        //given
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        when(randomNumberGenerator.nextInt(anyInt(), anyInt())).thenReturn(4);
        when(randomNumberGenerator.nextInts(anyInt(), anyInt())).thenReturn(List.of(1, 2, 3, 4));
        //when
        ShortUrlResponse response = shortUrlService.transformShortUrl(request);
        //then
        assertThat(response.getShortUrlKey().length()).isEqualTo(4);
    }

    @Test
    @DisplayName("shortUrlKey는 randomNumberGenerator에서 반환된 인덱스 리스트를 Base56 문자집합과 조합하여 생성한다.")
    void requestOriUrlByByRandomNumberGeneratorAndBase56CharSet() {
        //given
        List<Integer> ints = List.of(13, 2, 22, 4);
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        when(randomNumberGenerator.nextInt(anyInt(), anyInt())).thenReturn(4);
        when(randomNumberGenerator.nextInts(anyInt(), anyInt())).thenReturn(ints);
        //when
        ShortUrlResponse response = shortUrlService.transformShortUrl(request);
        //then
        StringBuilder builder = new StringBuilder();
        for (Integer idx : ints) {
            char randomChar = BASE56_CHARSET.getCharSet().charAt(idx);
            builder.append(randomChar);
        }
        String expectedKey = builder.toString();
        assertThat(response.getShortUrlKey()).isEqualTo(expectedKey);
    }

    @Test
    @DisplayName("randomNumberGenerator nextInt에 전달되는 인자를 캡처하여 검증한다. ShortUrlKey는 4자 이상 8자 이하이므로 4와 9가 캡쳐되야함")
    void verifyRandomNumberGeneratorArgumentsForRandomLength() {
        //given
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        ArgumentCaptor<Integer> minCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> maxCaptor = ArgumentCaptor.forClass(Integer.class);
        when(randomNumberGenerator.nextInt(anyInt(), anyInt())).thenReturn(4);
        //when
        shortUrlService.transformShortUrl(request);
        //then
        // ShortUrlKey 생성을 위한 사이즈 인자 체크 -> 4자이상 8자 이하이므로 4와 9가 인자로 전달된다.
        verify(randomNumberGenerator).nextInt(minCaptor.capture(), maxCaptor.capture());
        assertThat(minCaptor.getValue()).isEqualTo(4);
        assertThat(maxCaptor.getValue()).isEqualTo(9);
    }

    @Test
    @DisplayName("randomNumberGenerator nextInts의 두번째 인자로 전달되는 인자를 캡처하여 검증한다. ShortUrlKey를 만들기 위한 문자열 집합 인덱스 최대 크기는 BASE56_CHARSET charSet의 길이.")
    void verifyRandomNumberGeneratorArgumentsForRandomShortUrlKey() {
        //given
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        List<Integer> idxes = List.of(1, 2, 3, 4);
        ArgumentCaptor<Integer> charSetSizeCaptor = ArgumentCaptor.forClass(Integer.class);
        when(randomNumberGenerator.nextInts(anyInt(), anyInt())).thenReturn(idxes); //4개의 문자열 인덱스 반환
        //when
        shortUrlService.transformShortUrl(request);
        //then
        verify(randomNumberGenerator).nextInts(anyInt(), charSetSizeCaptor.capture());
        assertThat(charSetSizeCaptor.getValue()).isEqualTo(BASE56_CHARSET.getCharSet().length()); //생성될 문자열 집합 인덱스 최대 크기는 BASE56_CHARSET charSet의 길이와 같다
    }

    @Test
    @DisplayName("ShortUrl 저장 중 OriUrl 가 중복되었다면 에러를 발생시키고 재요청 문구를 안내한다.")
    void duplicatedOriUrlTest() {
        //given
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        ShortUrlRepository mock = Mockito.mock(ShortUrlRepository.class);
        when(mock.save(any())).thenThrow(DataIntegrityViolationException.class);
        ShortUrlService sut = new ShortUrlService(randomNumberGenerator, mock);
        //expected
        assertThatThrownBy(() -> sut.transformShortUrl(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(DUPLICATED_KEY_RETRY);
    }

    @Test
    @DisplayName("ShortUrl 저장 중 중복된 ShortUrl가 생성됐다면 최대 5번 재시도 한다.")
    void duplicatedShortUrlTest() {
        //given
        ShortUrlRequest request = new ShortUrlRequest(ANY_ORI_URL);
        ShortUrlRepository mock = Mockito.mock(ShortUrlRepository.class);
        when(mock.findByShortUrlKey(any())).thenReturn(Optional.of(ShortUrl.create(ANY_ORI_URL, ANY_SHORT_KEY)));
        ShortUrlService sut = new ShortUrlService(randomNumberGenerator, mock);
        //expected
        assertThatThrownBy(() -> sut.transformShortUrl(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(DUPLICATED_KEY_RETRY);
    }

    @Test
    @DisplayName("shortUrlKey로 oriUrl을 조회한다.")
    void requestOriUrl() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_KEY);
        shortUrlRepository.save(shortUrl);
        //when
        OriUrlResponse response = shortUrlService.requestOriUrl(ANY_SHORT_KEY);
        //then
        URL oriUrl = response.getOriUrl();
        assertThat(shortUrl.getOriUrl()).isEqualTo(oriUrl);
    }

    @Test
    @DisplayName("shortUrlKey로 oriUrl을 조회 시 ShortUrl의 requestCnt을 1 증가시킨다.")
    void requestOriUrlIncreaseRequestCnt() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_KEY);
        long beforeCnt = shortUrl.getRequestCnt();
        shortUrlRepository.save(shortUrl);
        //when
        shortUrlService.requestOriUrl(ANY_SHORT_KEY);
        //then
        long afterCnt = shortUrl.getRequestCnt();
        assertThat(beforeCnt + 1).isEqualTo(afterCnt);
    }

    @Test
    @DisplayName("존재하지 않는 shortUrlKey로 oriUrl을 조회 시 에러가 발생한다.")
    void requestOriUrlFail() {
        //given
        //expected
        assertThatThrownBy(() -> shortUrlService.requestOriUrl(ANY_SHORT_KEY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("oriUrl로 UrlInfoResponse를 조회한다.")
    void getUrlInfoByOriUrl() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_KEY);
        shortUrlRepository.save(shortUrl);
        //when
        UrlInfoResponse response = shortUrlService.getUrlInfoByOriUrl(ANY_ORI_URL);
        //then
        assertThat(response)
                .extracting(UrlInfoResponse::getOriUrl, UrlInfoResponse::getShortUrl, UrlInfoResponse::getRequestCnt)
                .containsExactly(ANY_ORI_URL, ANY_SHORT_KEY, 0L);
    }

    @Test
    @DisplayName("존재하지 않는 oriUrl로 UrlInfoResponse를 조회 시 에러가 발생한다.")
    void getUrlInfoByOriUrlFail() {
        //given
        //expected
        assertThatThrownBy(() -> shortUrlService.requestOriUrl(ANY_SHORT_KEY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("shortUrlKey로 UrlInfoResponse를 조회한다.")
    void getUrlInfoByShortUrlKey() {
        //given
        ShortUrl shortUrl = ShortUrl.create(ANY_ORI_URL, ANY_SHORT_KEY);
        shortUrlRepository.save(shortUrl);
        //when
        UrlInfoResponse response = shortUrlService.getUrlInfoByShortUrlKey(ANY_SHORT_KEY);
        //then
        assertThat(response)
                .extracting(UrlInfoResponse::getOriUrl, UrlInfoResponse::getShortUrl, UrlInfoResponse::getRequestCnt)
                .containsExactly(ANY_ORI_URL, ANY_SHORT_KEY, 0L);
    }

    @Test
    @DisplayName("존재하지 않는 shortUrlKey로 UrlInfoResponse를 조회 시 에러가 발생한다.")
    void getUrlInfoByShortUrlKeyFail() {
        //given
        //expected
        assertThatThrownBy(() -> shortUrlService.requestOriUrl(ANY_SHORT_KEY))
                .isInstanceOf(IllegalArgumentException.class);
    }
}