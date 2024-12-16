package com.shorturl.shorturl.dto.response;

import lombok.Getter;

import java.net.URL;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 7:26 오후
 */
@Getter
public class OriUrlResponse {
    private URL oriUrl;

    public OriUrlResponse(URL oriUrl) {
        this.oriUrl = oriUrl;
    }
}
