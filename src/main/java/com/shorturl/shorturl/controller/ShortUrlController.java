package com.shorturl.shorturl.controller;

import com.shorturl.shorturl.dto.request.ShortUrlRequest;
import com.shorturl.shorturl.dto.response.OriUrlResponse;
import com.shorturl.shorturl.dto.response.ShortUrlResponse;
import com.shorturl.shorturl.dto.response.UrlInfoResponse;
import com.shorturl.shorturl.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.*;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:20 오후
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    @PostMapping("short-url")
    public ShortUrlResponse transformShortUrl(@RequestBody @Valid ShortUrlRequest request) {
        log.info("ShortUrlController#transformShortUrl");
        return shortUrlService.transformShortUrl(request);
    }

    @GetMapping("{shortUrlKey}")
    public OriUrlResponse requestOriUrl(@NotBlank(message = SHORT_URL_KEY_IS_REQUIRED) @PathVariable String shortUrlKey) {
        log.info("ShortUrlController#getShortUrlKey");
        return shortUrlService.requestOriUrl(shortUrlKey);
    }

    @GetMapping("url-info/origin")
    public UrlInfoResponse getUrlInfoByOriUrl(@NotNull(message = ORIGIN_URL_IS_REQUIRED) @RequestParam String oriUrl) {
        log.info("ShortUrlController#getUrlInfoByOriUrl");
        return shortUrlService.getUrlInfoByOriUrl(toUrl(oriUrl));
    }

    @GetMapping("url-info/short")
    public UrlInfoResponse getUrlInfoByShortUrlKey(@NotBlank(message = SHORT_URL_KEY_IS_REQUIRED) @RequestParam String shortUrlKey) {
        log.info("ShortUrlController#getUrlInfoByShortUrl");
        return shortUrlService.getUrlInfoByShortUrlKey(shortUrlKey);
    }

    private URL toUrl(String oriUrl) {
        URL url;
        try {
            url = URI.create(oriUrl).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException(INVALID_URL_FORM);
        }
        return url;
    }
}
