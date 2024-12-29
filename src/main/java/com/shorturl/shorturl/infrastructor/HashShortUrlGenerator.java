package com.shorturl.shorturl.infrastructor;

import com.shorturl.shorturl.domain.ShortUrl;
import com.shorturl.shorturl.domain.ShortUrlGenerator;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * 길이를 제한하면 해시충돌 가능성이 높아짐
 * 해시충돌 가능성이 생기면 저장 전에 DB 질의가 필요함 (중복 여부)
 *
 */
@Component
public class HashShortUrlGenerator implements ShortUrlGenerator {

    @Override
    public ShortUrl createShortUrl(URL oriUrl) {

        return null;
    }
}
