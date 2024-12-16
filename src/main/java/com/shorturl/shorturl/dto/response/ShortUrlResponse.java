package com.shorturl.shorturl.dto.response;

import lombok.Getter;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:34 오후
 */
@Getter
public class ShortUrlResponse {
    private String shortUrlKey;

    public ShortUrlResponse(String shortUrlKey) {
        this.shortUrlKey = shortUrlKey;
    }
}
