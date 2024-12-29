package com.shorturl.shorturl.infrastructor;

import com.shorturl.shorturl.domain.ShortUrl;
import com.shorturl.shorturl.domain.ShortUrlGenerator;
import com.shorturl.shorturl.repository.ShortUrlRepository;
import com.shorturl.shorturl.util.encording.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * 62진법으로 인코딩 -> id가 커지말 길이도 커짐 -> id 몇 부터? 길이가 8이내 -> 2백조개 가량의 id 생성 가능
 * id로 생성해야하므로 저장 전에 id 질의 필요
 *
 * -> UUID로 생성한다면 괜찮음?
 * --> UUID는 128비트, 숫자가 아닌값이 포함됨 (36자리의 숫자 문자열 형태) -> UUID를 Base62로 인코딩한 결과는 길이는 22자
 *
 * <<id 순차 증가 시 보안 이슈가 있음>>
 *
 */
@Component
@RequiredArgsConstructor
public class EncodeShortUrlGenerator implements ShortUrlGenerator {
    private final RedisKeyGenerator redisKeyGenerator;
    private final ShortUrlRepository shortUrlRepository;
    private final Base62 base62;

    @Override
    public ShortUrl createShortUrl(URL oriUrl) {
        Long id = redisKeyGenerator.generateKey("shortUrl:pk");

        String shortUrlKey = createShortUrlKey(id);
        ShortUrl shortUrl = ShortUrl.create(id, oriUrl, shortUrlKey);
        shortUrlRepository.save(shortUrl);
        return shortUrl;

//        ShortUrl shortUrl = ShortUrl.create();
//        shortUrlRepository.save(shortUrl);
//
//        Long id = shortUrl.getId();
//        String shortUrlKey = createShortUrlKey(id);
//        shortUrl.matchUrls(oriUrl,shortUrlKey);
//        return shortUrl;
    }

    public String createShortUrlKey(long id) {
        return base62.encoding(id);
    }
}
