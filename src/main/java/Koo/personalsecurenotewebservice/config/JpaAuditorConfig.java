package Koo.personalsecurenotewebservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA auditor enable
 */
@Configuration
@EnableJpaAuditing // JpaAuditing을 Enable
public class JpaAuditorConfig { // jpa 설정때문에 추가하였다.
}
