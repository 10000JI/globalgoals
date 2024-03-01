package dev.globalgoals.config;

import dev.globalgoals.security.UserLoginFailHandler;
import dev.globalgoals.security.UserLogoutSucessHandler;
import dev.globalgoals.security.UserSuceessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                //CorsConfigurationSource 인터페이스를 구현하는 익명 클래스 생성하여 getCorsConfiguration() 메소드 재정의
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeRequests((requests) -> requests
                        .requestMatchers("/img/**", "/js/**", "/css/**", "/fonts/**","/", "/users/**").permitAll()
                        .anyRequest().authenticated()
                )
                //DaoAuthenticationProvider의 세부 내역을 AuthenticationProvider 빈을 만들어 정의했으므로 인증을 구성해줘야 한다.
                .formLogin(login -> login
                                .loginPage("/users/login")
                                .usernameParameter("id")
//                        .defaultSuccessUrl("/") //인증에 성공할 경우 요청할 URL
                                .successHandler(new UserSuceessHandler()) //로그인 성공 시
                                //                       .failureUrl("/users/login") //실패했을 경우 요청할 URL
                                .failureHandler(new UserLoginFailHandler()) //로그인 실패 시
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessHandler(new UserLogoutSucessHandler()) //로그아웃 성공 시
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}