package com.shorturl.shorturl.dto.response;

import com.shorturl.shorturl.domain.ShortUrl;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.net.URL;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:35 오후
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UrlInfoResponse {
    private URL oriUrl;
    private String shortUrl;
    private long requestCnt;

    public static UrlInfoResponse of(
            ShortUrl shortUrl
    ) {
        return UrlInfoResponse.of(
                shortUrl.getOriUrl(),
                shortUrl.getShortUrlKey(),
                shortUrl.getRequestCnt()
        );
    }

    public static UrlInfoResponse of(
            URL oriUrl,
            String shortUrl,
            long requestCnt
    ) {
        return UrlInfoResponse.builder()
                .oriUrl(oriUrl)
                .shortUrl(shortUrl)
                .requestCnt(requestCnt)
                .build();
    }
}
