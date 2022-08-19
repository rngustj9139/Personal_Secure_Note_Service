package Koo.personalsecurenotewebservice.customfilter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class StopwatchFilter extends OncePerRequestFilter { // Spring Security Filter를 이용하는 것이 아닌 Custom Filter 정의

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch(request.getServletPath());
        stopWatch.start();
        filterChain.doFilter(request, response);
        stopWatch.stop();
        // Log StopWatch '/login': running time = 150545041 ns
        log.info("stopwatch short summary: {}", stopWatch.shortSummary());
    }

}
