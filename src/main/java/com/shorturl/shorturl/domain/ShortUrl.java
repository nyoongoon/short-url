package com.shorturl.shorturl.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URL;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.ORIGIN_URL_IS_REQUIRED;
import static com.shorturl.shorturl.exception.ExceptionMessageConstants.SHORT_URL_KEY_IS_REQUIRED;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 11:37 오전
 */
@Getter
@Entity
@Table(
        name = "ShortUrl",
        indexes = {
                @Index(name = "idx_ori_url", columnList = "ori_url"),
                @Index(name = "idx_short_url_key", columnList = "short_url_key")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @NotNull(message = ORIGIN_URL_IS_REQUIRED)
//    @Column(name = "ori_url", nullable = false, unique = true)
    @Column(name = "ori_url", nullable = true, unique = true)
    private URL oriUrl;

//    @NotEmpty(message = SHORT_URL_KEY_IS_REQUIRED)
//    @Column(name = "short_url_key", nullable = false, unique = true)
    @Column(name = "short_url_key", nullable = true, unique = true)
    private String shortUrlKey;

    @Column(name = "request_cnt", nullable = false)
    private long requestCnt;

    @Builder(access = AccessLevel.PRIVATE)
    private ShortUrl(Long id, URL oriUrl, String shortUrlKey, long requestCnt) {
//        Assert.notNull(oriUrl, ORIGIN_URL_IS_REQUIRED);
//        Assert.hasText(shortUrlKey, SHORT_URL_KEY_IS_REQUIRED);
        this.id = id;
        this.oriUrl = oriUrl;
        this.shortUrlKey = shortUrlKey;
        this.requestCnt = requestCnt;
    }

    public static ShortUrl create(
            URL oriUrl,
            String shortUrlKey
    ) {

        return ShortUrl.builder()
                .oriUrl(oriUrl)
                .shortUrlKey(shortUrlKey)
                .requestCnt(0L) //최초 생성 시 요청은 0
                .build();
    }

    public static ShortUrl create(
//            URL oriUrl
    ) {

        return ShortUrl.builder()
                .oriUrl(null)
                .shortUrlKey(null)
                .requestCnt(0L) //최초 생성 시 요청은 0
                .build();
    }

    public static ShortUrl create(
        Long id,
        URL oriUrl,
        String shortUrlKey
    ) {

        return ShortUrl.builder()
                .id(id)
                .oriUrl(oriUrl)
                .shortUrlKey(shortUrlKey)
                .requestCnt(0L) //최초 생성 시 요청은 0
                .build();
    }

    public void matchUrls(URL oriUrl, String shortUrlKey){
        this.oriUrl = oriUrl;
        this.shortUrlKey = shortUrlKey;
    }

    public void increaseRequestCnt() {
        requestCnt++;
    }
}
