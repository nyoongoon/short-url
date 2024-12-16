package com.shorturl.shorturl.exception;


/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 11:54 오전
 */
public class ExceptionMessageConstants {
    /* ShortUrl */
    public static final String ORIGIN_URL_IS_REQUIRED = "원본 URL은 필수값입니다.";
    public static final String SHORT_URL_KEY_IS_REQUIRED = "Short Url Key는 필수값입니다.";
    public static final String NOT_FOUND_ORI_URL = "요청된 적이 없는 원본 URL 입니다.";
    public static final String NOT_FOUND_SHORT_URL_KEY = "생성된 적이 없는 ShortUrlKey 입니다.";

    /* URL */
    public static final String INVALID_URL_FORM = "잘못된 URL 형식입니다.";

    /* 중복 키 */
    public static final String DUPLICATED_KEY_RETRY = "단축 URL 생성 중 에러가 발생했습니다.\n다시 한 번 시도해주세요.";

    /* 서버 에러 */
    public static final String SERVER_ERROR = "서버 에러가 발행하였습니다.";
}
