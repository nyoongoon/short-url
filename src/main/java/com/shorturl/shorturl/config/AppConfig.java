package com.shorturl.shorturl.config;

import org.springframework.beans.factory.BeanFactory;
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
public class AppConfig {
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
}
