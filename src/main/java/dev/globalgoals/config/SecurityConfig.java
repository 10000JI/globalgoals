package dev.globalgoals.config;

import dev.globalgoals.security.UserLoginFailHandler;
import dev.globalgoals.security.UserLogoutSucessHandler;
import dev.globalgoals.security.UserSuceessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
        //public 을 선언하면 default로 바꾸라는 메세지 출력
    WebSecurityCustomizer webSecurityConfig() {
        //Security에서 무시해야하는 URL 패턴 등록
        return web -> web
                .ignoring()
                .antMatchers("/img/**")
                .antMatchers("/js/**")
                .antMatchers("/css/**")
                .antMatchers("/fonts/**");
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable()
                .authorizeHttpRequests(request -> request
                        .antMatchers("/", "/users/**").permitAll()
                        .anyRequest().authenticated()
                )
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
}