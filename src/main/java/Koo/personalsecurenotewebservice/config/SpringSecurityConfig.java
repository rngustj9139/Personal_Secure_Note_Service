package Koo.personalsecurenotewebservice.config;

import Koo.personalsecurenotewebservice.customfilter.StopwatchFilter;
import Koo.personalsecurenotewebservice.jwt.JwtAuthenticationFilter;
import Koo.personalsecurenotewebservice.jwt.JwtAuthorizationFilter;
import Koo.personalsecurenotewebservice.user.User;
import Koo.personalsecurenotewebservice.user.UserRepository;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security 설정 Config
 */
@Configuration
@EnableWebSecurity // WebSecurityConfigureAdapter를 상속을 하는 경우에 사용하는 어노테이션
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception { // 시큐리티의 상세 설정 정의
        // Custom Filter 등록
        http.addFilterBefore(
                new StopwatchFilter(),
                WebAsyncManagerIntegrationFilter.class
        );

        // basic authentication filter
        http.httpBasic().disable(); // basic authentication filter 비활성화 (httpBasic() : Http basic Auth 기반으로 로그인 인증창이 뜸. 기본 인증 로그인을 이용하지 않으면 disable )

        // csrf
        http.csrf(); // 사이트 간 요청 위조(Cross-site Request Forgery) 방지 ex) 이러한 공격을 하기 위하여 해커는 우선 공격을 할 사이트를 먼저 분석합니다. 예를 들어, 나무위키의 경우에 토론은 namu.wiki/topic/ 이라고 시작하며 뒤에 숫자가 붙는 형식인데 이 뒤의 숫자에 패턴이 있습니다.(실제론 토론이 개설된 순서대로 붙는 일련번호이다.) 그러면 이 패턴을 이용하여 일반적인 방법으로 접근할 수 없는 페이지를 오픈 한다든지, 개발에 사용되고 실제로 사용하지 않는 샘플 페이지를 찾아낸다든지 이러한 방법이 가능합니다.

        // remember-me
        http.rememberMe(); // 사용자 세션이 만료되고 웹 브라우저가 종료된 후에도 애플리케이션이 사용자의 정보를 기억한다. (로그인 유지하기)

        // session 대신 토큰 이용
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 우리는 JWT를 사용하므로(토큰방식) 세션이 필요없다 따라서 Stateless로 설정해둔다.

        // jwt filter 등록
        http.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class // JwtAuthenticationFilter 필터는 UsernamePasswordAuthenticationFilter 필터의 앞쪽에 위치
        ).addFilterBefore(
                new JwtAuthorizationFilter(userRepository),
                BasicAuthenticationFilter.class
        );

        // authorization
        http.authorizeRequests() // 인가 설정 (경로별로 권한을 설정)
                // /와 /home /signup은 모두에게 허용
                .antMatchers("/", "/home", "/signup").permitAll()
                // hello 페이지는 USER 롤을 가진 유저에게만 허용
                .antMatchers("/note").hasRole("USER") // 개인 노트 페이지는 유저만 접근 가능
                .antMatchers("/admin").hasRole("ADMIN") // 어드민 페이지는 어드민만 접근 가능
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
                .anyRequest().authenticated(); // 그외의 모든 요청은 인증(로그인)을 한사람만 접근 가능
        // login
        http.formLogin() // 로그인 컨트롤러를 따로 만들지 않아도 자동으로 form login처리를 해준다.
                .loginPage("/login")
                .defaultSuccessUrl("/") // 로그인에 성공할 경우 홈으로 돌아간다.
                .permitAll(); // 모두에게 이 기능을 허용
        // logout
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 로그인을 한 상태면 로그아웃을 할 수 있도록 자동으로 설정해준다.
                .logoutSuccessUrl("/"); // 로그아웃에 성공하면 홈으로 돌아간다.
    }

    @Override
    public void configure(WebSecurity web) { // 정적 리소스는 spring security 대상에서 제외
//      web.ignoring().antMatchers("/images/**", "/css/**"); // 아래 코드와 같은 코드입니다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 특정 리소스에 대해서는 SpringSecurity를 적용하고 싶지 않을때가 있는데 그때 사용한다.
    }

    /**
     * UserDetailsService 구현
     *
     * @return UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() { // 유저를 가져오는 방법을 정의함 => (스프링 시큐리티는 우리가 만든 User 클래스를 모르기 때문이다.)
        return username -> { // lambda
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }

            return user;
        };
    }

}
