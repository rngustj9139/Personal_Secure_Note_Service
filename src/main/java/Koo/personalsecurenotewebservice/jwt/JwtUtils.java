package Koo.personalsecurenotewebservice.jwt;

import Koo.personalsecurenotewebservice.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import org.springframework.data.util.Pair;

import java.security.Key;
import java.util.Date;

public class JwtUtils { // jwt (json web token) 토큰을 생성하거나 Parsing 하는 메소드를 제공

    /**
     * 토큰에서 username 찾기
     *
     * @param token 토큰
     * @return username
     */
    public static String getUsername(String token) {
        SigningKeyResolver signingKeyResolver = new SigningKeyResolver();
        // jwtToken에서 username을 찾습니다.
        return Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver) // SigningKeyResolver는 JWT 헤더에서 kid를 찾아서 key를 찾아오는 역할을 하며 signature를 검증한다.
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // username
    }

    /**
     * user로 토큰 생성
     * HEADER : alg, kid
     * PAYLOAD : sub, iat, exp
     * SIGNATURE : JwtKey.getRandomKey로 구한 Secret Key로 HS512 해시
     *ㅁ
     * @param user 유저
     * @return jwt token
     */
    public static String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername()); // subject(주체)로 username을 설정 (JWT는 Json을 이용하여 사용자에 대한 속성을 저장하는 Claim 기반의 웹 토큰입니다. 클레임 기반에서 클레임은 주체가 수행할 수 있는 작업보다는 주체가 무엇인지를 표현하는 이름과 값의 쌍이다.)
        Date now = new Date(); // 현재 시간
        Pair<String, Key> key = JwtKey.getRandomKey();
        // JWT Token 생성
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JwtProperties.EXPIRATION_TIME)) // 토큰 만료 시간 설정
                .setHeaderParam(JwsHeader.KEY_ID, key.getFirst()) // kid
                .signWith(key.getSecond()) // signature
                .compact();
    }

}
