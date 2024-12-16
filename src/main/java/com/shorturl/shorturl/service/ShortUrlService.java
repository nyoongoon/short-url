package com.shorturl.shorturl.service;

import com.shorturl.shorturl.domain.ShortUrl;
import com.shorturl.shorturl.dto.request.ShortUrlRequest;
import com.shorturl.shorturl.dto.response.OriUrlResponse;
import com.shorturl.shorturl.dto.response.ShortUrlResponse;
import com.shorturl.shorturl.dto.response.UrlInfoResponse;
import com.shorturl.shorturl.repository.ShortUrlRepository;
import com.shorturl.shorturl.util.randomGenerator.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.*;
import static com.shorturl.shorturl.util.constant.CharSet.BASE56_CHARSET;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:19 오후
 */
@Slf4j
@Service
@Repository
@RequiredArgsConstructor
public class ShortUrlService {
    private final RandomNumberGenerator randomNumberGenerator;
    private final ShortUrlRepository shortUrlRepository;

    @Transactional
    public ShortUrlResponse transformShortUrl(ShortUrlRequest request) {
        URL oriUrl = request.getOriUrl();
        Optional<ShortUrl> optional = shortUrlRepository.findByOriUrl(oriUrl);

        if (optional.isPresent()) {
            ShortUrl foundShortUrl = optional.get();
            return new ShortUrlResponse(foundShortUrl.getShortUrlKey());
        }

        String shortUrlKey = createUniqueShortUrlKey();
        ShortUrl shortUrl = ShortUrl.create(oriUrl, shortUrlKey);

        try {
            shortUrlRepository.save(shortUrl);
        } catch (DataIntegrityViolationException e) {// 저장 시점에 유니크 키 중복 발생 상황
            log.error("ShortUrlService#transformShortUrl 저장 시점에 유니크 키 중복 발생 - DataIntegrityViolationException: {}", e.getMessage());
            throw new IllegalStateException(DUPLICATED_KEY_RETRY);
        }

        return new ShortUrlResponse(shortUrl.getShortUrlKey());
    }

    private String createUniqueShortUrlKey() {
        String shortUrlKey;
        for (int i = 0; i < 5; i++) { //존재하는 ShortUrlKey 생성 시 최대 5회 재시도
            shortUrlKey = createShortUrlKey();
            Optional<ShortUrl> byShortUrlKey = shortUrlRepository.findByShortUrlKey(shortUrlKey);
            if (byShortUrlKey.isEmpty()) {
                return shortUrlKey;
            }
            log.warn("ShortUrlService#transformShortUrl 저장 중 ShorUrlKey 중복 생성됨 shortUrlKey: {}", shortUrlKey);
        }
        throw new IllegalStateException(DUPLICATED_KEY_RETRY);
    }

    private String createShortUrlKey() {
        //최소 사이즈는 4, 최대 사이즈는 8
        int randomLength = randomNumberGenerator.nextInt(4, 9); //최소가 4이면 56^4 대략 9백만개이상
        List<Integer> randomIndexes = randomNumberGenerator.nextInts(randomLength, BASE56_CHARSET.getCharSet().length());

        StringBuilder shortUrlBuilder = new StringBuilder();
        for (Integer randomIndex : randomIndexes) {
            char randomChar = BASE56_CHARSET.getCharSet().charAt(randomIndex);
            shortUrlBuilder.append(randomChar);
        }
        return shortUrlBuilder.toString();
    }

    @Transactional
    public OriUrlResponse requestOriUrl(String shortUrlKey) {
        ShortUrl shortUrl = shortUrlRepository.findByShortUrlKey(shortUrlKey)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_SHORT_URL_KEY));
        shortUrl.increaseRequestCnt(); //requestCnt 1 증가
        URL oriUrl = shortUrl.getOriUrl();
        return new OriUrlResponse(oriUrl);
    }

    @Transactional(readOnly = true)
    public UrlInfoResponse getUrlInfoByOriUrl(URL oriUrl) {
        ShortUrl shortUrl = shortUrlRepository.findByOriUrl(oriUrl)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORI_URL));
        return UrlInfoResponse.of(shortUrl);
    }

    @Transactional(readOnly = true)
    public UrlInfoResponse getUrlInfoByShortUrlKey(String shortUrlKey) {
        ShortUrl shortUrl = shortUrlRepository.findByShortUrlKey(shortUrlKey)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_SHORT_URL_KEY));
        return UrlInfoResponse.of(shortUrl);
    }
}
