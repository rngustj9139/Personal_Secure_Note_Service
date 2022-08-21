package Koo.personalsecurenotewebservice.jwt;

public class JwtProperties { // 기본 설정 값

    public static final int EXPIRATION_TIME = 600000; // 600초 (10분) (토큰 만료 시간 -> 60만 미리 세컨드)
    public static final String COOKIE_NAME = "JWT-AUTHENTICATION";

}
