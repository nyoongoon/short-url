package com.shorturl.shorturl.config;

import com.shorturl.shorturl.domain.ShortUrlGenerator;
import com.shorturl.shorturl.infrastructor.EncodeShortUrlGenerator;
import com.shorturl.shorturl.infrastructor.RandomShortUrlGenerator;
import com.shorturl.shorturl.repository.ShortUrlRepository;
import com.shorturl.shorturl.util.encording.Base62;
import com.shorturl.shorturl.util.randomGenerator.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

import java.lang.annotation.Annotation;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 8:48 오후
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final RandomNumberGenerator randomNumberGenerator;
    private final ShortUrlRepository shortUrlRepository;
    private final Base62 base62;

    // PersistenceExceptionTranslator 등록하지 않음
    @Bean // throw로 던진 예외를 Spring이 자동 변환하지 못하게 설정
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor() {
            @Override
            public void setRepositoryAnnotationType(Class<? extends Annotation> repositoryAnnotationType) {
            }

            @Override
            public void setBeanFactory(BeanFactory beanFactory) {
            }
        };
    }

    @Bean
    public ShortUrlGenerator shortUrlGenerator(){
        return new RandomShortUrlGenerator(randomNumberGenerator, shortUrlRepository);
//        return new EncodeShortUrlGenerator(shortUrlRepository, base62);
    }
}
