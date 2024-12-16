package com.shorturl.shorturl.repository;

import com.shorturl.shorturl.domain.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.Optional;


/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:19 오후
 */
@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByOriUrl(URL oriUrl);

    Optional<ShortUrl> findByShortUrlKey(String shortUrlKey);
}
