package Koo.personalsecurenotewebservice.jwt;

import Koo.personalsecurenotewebservice.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { // jwt와 spring security를 연동하기 위해 필터 구현

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    /**
     * 로그인 인증 시도
     **/
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 로그인할 때 입력한 username과 password를 가지고 authenticationToken를 생성한다.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getParameter("username"), request.getParameter("password"), new ArrayList<>());

        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * 인증에 성공했을 때 사용
     * JWT Token을 생성해서 쿠키에 넣는다.
     * 이후 브라우저에서 쿠키를 쿠키저장소에 넣어 사용할 수 있게 된다.
     **/
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();
        String token = JwtUtils.createToken(user);
        // 쿠키 생성
        Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, token);
        cookie.setMaxAge(JwtProperties.EXPIRATION_TIME); // 쿠키의 만료시간 설정
        cookie.setPath("/"); // cookie.setPath("/원하는URL") : 원하는 URL로만 cookie를 보내도록 설정해서 효율적으로 Cookie관리를 한다.
        response.addCookie(cookie);
        response.sendRedirect("/");
    }

    /**
     * 인증에 실패했을 경우 리다이렉션 수행
     **/
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.sendRedirect("/login");
    }

}
