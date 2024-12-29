package com.shorturl.shorturl.service;

import com.shorturl.shorturl.domain.ShortUrl;
import com.shorturl.shorturl.domain.ShortUrlGenerator;
import com.shorturl.shorturl.dto.request.ShortUrlRequest;
import com.shorturl.shorturl.dto.response.OriUrlResponse;
import com.shorturl.shorturl.dto.response.ShortUrlResponse;
import com.shorturl.shorturl.dto.response.UrlInfoResponse;
import com.shorturl.shorturl.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Optional;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.NOT_FOUND_ORI_URL;
import static com.shorturl.shorturl.exception.ExceptionMessageConstants.NOT_FOUND_SHORT_URL_KEY;

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
    private final ShortUrlGenerator shortUrlGenerator;
    private final ShortUrlRepository shortUrlRepository;

    @Transactional
    public ShortUrlResponse transformShortUrl(ShortUrlRequest request) {

        Optional<ShortUrl> optional = shortUrlRepository.findByOriUrl(request.getOriUrl());

        ShortUrl shortUrl = optional.orElseGet(() -> shortUrlGenerator.createShortUrl(request.getOriUrl()));

        return new ShortUrlResponse(shortUrl.getShortUrlKey());
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
