package com.shorturl.shorturl.infrastructor;

import com.shorturl.shorturl.domain.ShortUrl;
import com.shorturl.shorturl.domain.ShortUrlGenerator;
import com.shorturl.shorturl.dto.response.ShortUrlResponse;
import com.shorturl.shorturl.exception.DuplicatedKeyException;
import com.shorturl.shorturl.repository.ShortUrlRepository;
import com.shorturl.shorturl.util.randomGenerator.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.DUPLICATED_KEY_RETRY;
import static com.shorturl.shorturl.util.constant.CharSet.BASE56_CHARSET;

/**
 * 이 문자열이 기존의 데이터들과는 중복되지 않는지 검증 -> 중복 시 재생성
 * -> 하지만 4~8 글자인 경우 (9백만 개 ~ 96조개) 약 96조개가 가능하므로 충돌 횟수는 미미할 것으로 생학함
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RandomShortUrlGenerator implements ShortUrlGenerator {
    private final RandomNumberGenerator randomNumberGenerator;
    private final ShortUrlRepository shortUrlRepository;

    @Override
    public ShortUrl createShortUrl(URL oriUrl) {
        String shortUrlKey = createUniqueShortUrlKey();
        ShortUrl shortUrl = ShortUrl.create(oriUrl, shortUrlKey);

        try {
            shortUrlRepository.save(shortUrl);
        } catch (DataIntegrityViolationException e) {// 저장 시점에 유니크 키 중복 발생 상황
            log.error("ShortUrlService#transformShortUrl 저장 시점에 유니크 키 중복 발생 - DataIntegrityViolationException: {}", e.getMessage());
            throw new IllegalStateException(DUPLICATED_KEY_RETRY);
        }

        return shortUrl;
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
//        throw new IllegalStateException(DUPLICATED_KEY_RETRY);
        throw new DuplicatedKeyException();
    }

    public String createShortUrlKey() {
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

}
