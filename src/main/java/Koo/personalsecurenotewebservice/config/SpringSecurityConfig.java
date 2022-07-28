package Koo.personalsecurenotewebservice.config;

import Koo.personalsecurenotewebservice.user.User;
import Koo.personalsecurenotewebservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security 설정 Config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception { // 시큐리티의 상세 설정 정의
        // basic authentication
        http.httpBasic().disable(); // basic authentication filter 비활성화 (httpBasic() : Http basic Auth 기반으로 로그인 인증창이 뜸. 기본 인증 로그인을 이용하지 않으면 disable )
        // csrf
        http.csrf(); // 사이트 간 요청 위조(Cross-site Request Forgery) 방지 ex) 이러한 공격을 하기 위하여 해커는 우선 공격을 할 사이트를 먼저 분석합니다. 예를 들어, 나무위키의 경우에 토론은 namu.wiki/topic/ 이라고 시작하며 뒤에 숫자가 붙는 형식인데 이 뒤의 숫자에 패턴이 있습니다.(실제론 토론이 개설된 순서대로 붙는 일련번호이다.) 그러면 이 패턴을 이용하여 일반적인 방법으로 접근할 수 없는 페이지를 오픈 한다든지, 개발에 사용되고 실제로 사용하지 않는 샘플 페이지를 찾아낸다든지 이러한 방법이 가능합니다.
        // remember-me
        http.rememberMe(); // 사용자 세션이 만료되고 웹 브라우저가 종료된 후에도 애플리케이션이 사용자의 정보를 기억한다.
        // authorization
        http.authorizeRequests()
                // /와 /home은 모두에게 허용
                .antMatchers("/", "/home", "/signup").permitAll()
                // hello 페이지는 USER 롤을 가진 유저에게만 허용
                .antMatchers("/note").hasRole("USER")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
                .anyRequest().authenticated();
        // login
        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll(); // 모두 허용
        // logout
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");
    }

    @Override
    public void configure(WebSecurity web) {
        // 정적 리소스 spring security 대상에서 제외
//        web.ignoring().antMatchers("/images/**", "/css/**"); // 아래 코드와 같은 코드입니다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * UserDetailsService 구현
     *
     * @return UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            return user;
        };
    }

}
