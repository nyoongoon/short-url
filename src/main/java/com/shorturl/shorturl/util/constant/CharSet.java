package com.shorturl.shorturl.util.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/15 1:17 오후
 */
@Getter
@RequiredArgsConstructor
public enum CharSet {
    /**
     * 숫자 0,1
     * 대문자 O, I
     * 소문자 o, l 제외
     */
    BASE56_CHARSET("23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz");

    private final String charSet;
}
