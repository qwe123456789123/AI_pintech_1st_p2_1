package org.koreait.global.configs;

import org.koreait.member.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SecurityConfig 클래스는 Spring Security 설정을 담당합니다.
 * - 인증(로그인/로그아웃)
 * - 인가(권한별 페이지 접근 통제)
 */
@Configuration
@EnableMethodSecurity // 메서드 수준 보안을 활성화
public class SecurityConfig {

    @Autowired
    private MemberInfoService memberInfoService;

    /**
     * SecurityFilterChain Bean을 정의하여 HTTP 요청에 대한 보안 설정을 구성합니다.
     *
     * @param http Spring Security의 HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /* 인증 설정 S - 로그인, 로그아웃 */
        http.formLogin(c -> {
            c.loginPage("/member/login") // 사용자 정의 로그인 페이지 경로 설정
                    .usernameParameter("email") // 로그인 양식의 사용자 ID 파라미터 이름
                    .passwordParameter("password") // 로그인 양식의 비밀번호 파라미터 이름
                    .failureHandler(new LoginFailureHandler()) // 로그인 실패 처리 핸들러
                    .successHandler(new LoginSuccessHandler()); // 로그인 성공 처리 핸들러
        });

        http.logout(c -> {
            c.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 로그아웃 요청 경로
                    .logoutSuccessUrl("/member/login"); // 로그아웃 성공 시 리다이렉트할 경로
        });
        /* 인증 설정 E */

        /* 인가 설정 S - 페이지 접근 통제 */
        /**
         * 접근 제어 조건:
         * - authenticated(): 인증된 사용자만 접근 가능
         * - anonymous(): 인증되지 않은 사용자만 접근 가능
         * - permitAll(): 모든 사용자가 접근 가능
         * - hasAuthority(): 특정 권한을 가진 사용자만 접근 가능
         * - hasAnyAuthority(): 나열된 권한 중 하나라도 만족하면 접근 가능
         * - hasRole(), hasAnyRole(): 특정 역할을 가진 사용자만 접근 가능
         */
        http.authorizeHttpRequests(c -> {
            c.requestMatchers("/mypage/**").authenticated() // 인증된 사용자만 접근
                    .requestMatchers("/member/login", "/member/join", "/member/agree").anonymous() // 인증되지 않은 사용자만 접근
                    .requestMatchers("/admin/**").hasAnyAuthority("MANAGER", "ADMIN") // 관리자 페이지는 MANAGER 또는 ADMIN 권한 필요
                    .anyRequest().permitAll(); // 그 외 모든 요청은 접근 허용
        });

        http.exceptionHandling(c -> {
            c.authenticationEntryPoint(new MemberAuthenticationExceptionHandler()) // 인증 실패 처리
                    .accessDeniedHandler(new MemberAccessDeniedHandler()); // 권한 부족으로 접근 거부 처리
        });
        /* 인가 설정 E */

        /* 자동 로그인 설정 S */
        http.rememberMe(c -> {
           c.rememberMeParameter("autoLogin")
                   .tokenValiditySeconds(60*60*24*30)
        // 자동 로그인을 유지할 시간, 기본값 14일
                   .userDetailsService(memberInfoService)
                   .authenticationSuccessHandler(new LoginSuccessHandler());
        });
        /* 자동 로그인 설정 E */

        return http.build(); // 보안 설정을 빌드하여 반환
    }

    /**
     * PasswordEncoder Bean을 정의합니다.
     * - BCryptPasswordEncoder를 사용하여 비밀번호를 해시화합니다.
     *
     * @return PasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
