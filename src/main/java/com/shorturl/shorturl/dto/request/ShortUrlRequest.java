package com.shorturl.shorturl.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.net.URL;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.INVALID_URL_FORM;


/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 1:01 오후
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrlRequest {
    @NotNull(message = INVALID_URL_FORM)
    private URL oriUrl;

    public ShortUrlRequest(URL oriUrl) {
        this.oriUrl = oriUrl;
    }
}
