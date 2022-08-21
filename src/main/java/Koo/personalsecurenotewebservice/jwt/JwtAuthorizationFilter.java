package Koo.personalsecurenotewebservice.jwt;

import Koo.personalsecurenotewebservice.user.User;
import Koo.personalsecurenotewebservice.user.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 유저 Authentication(인증, 로그인) 성공 후
 * 유저에서 서버쪽으로 JWT가 들어왔을때 Authorization(인가)를 수행한다.
**/
public class JwtAuthorizationFilter extends OncePerRequestFilter { // OncePerRequestFilter은 요청때마다 발생하는 필터이다.

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = null;

        try {
            // cookie 에서 JWT token을 가져옵니다.
            token = Arrays.stream(request.getCookies()) // 여러개의 쿠기가 들어오므로 스트림을 건다.
                    .filter(cookie -> cookie.getName().equals(JwtProperties.COOKIE_NAME)).findFirst() // findFirst는 filter로 걸러진 쿠키중 첫번째 쿠키를 찾게함
                    .map(Cookie::getValue) // .map(cookie->cookie.getValue())와 같다, cookie.getName()은 저장된 데이터 값에 대한 이름을 가져오는 메소드이고cookie.getValue()는 해당 이름에 저장된 데이터 값을 가져온다.
                    .orElse(null); // getValue를 통해 값을 찾을려하지만 없다면 null을 반환한다 => token 변수에 null이 저장되게 된다.
        } catch (Exception ignored) {
        }

        if (token != null) {
            try {
                Authentication authentication = getUsernamePasswordAuthenticationToken(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) { // Exception이 발생하면 응답의 쿠키를 null로 변경한다.
                Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * 서버단에 도착한 쿠키 속 JWT 토큰으로 User를 찾아서 UsernamePasswordAuthenticationToken를 만들어서 반환한다.
     * User가 없다면 null
     * User기 있다면 자동으로 로그인된다.(Authentication 생성, 생성된 Authentication을 Security Context에 넣는다.)
     */
    private Authentication getUsernamePasswordAuthenticationToken(String token) {
        String userName = JwtUtils.getUsername(token);
        if (userName != null) {
            User user = userRepository.findByUsername(userName); // 유저를 유저명으로 찾습니다.
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()); // user는 Principal
        }

        return null; // 유저가 없으면 NULL
    }

}
