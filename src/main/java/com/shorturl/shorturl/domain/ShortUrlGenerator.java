package com.shorturl.shorturl.domain;

import javax.validation.constraints.NotNull;
import java.net.URL;

public interface ShortUrlGenerator {
    ShortUrl  createShortUrl(URL oriUrl);
}
