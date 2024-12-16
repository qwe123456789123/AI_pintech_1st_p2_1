package org.koreait.test.annotations;

import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * MockSecurityContextFactory는 Spring Security의 테스트 환경에서 사용자 인증 상태를 모의하기 위해 사용됩니다.
 * @MockMember 애노테이션 정보를 기반으로 SecurityContext를 생성하여 인증된 사용자를 테스트에 주입합니다.
 */
public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {

    /**
     * @param annotation @MockMember 애노테이션 정보를 기반으로 SecurityContext를 생성합니다.
     * @return 생성된 SecurityContext (사용자 인증 정보 포함)
     */
    @Override
    public SecurityContext createSecurityContext(MockMember annotation) {
        // Member 객체 생성 및 초기화: 실제 애플리케이션에서의 사용자 정보
        Member member = new Member();
        member.setSeq(annotation.seq()); // 사용자 고유 번호 설정
        member.setEmail(annotation.email()); // 사용자 이메일 설정
        member.setPassword(annotation.password()); // 사용자 비밀번호 설정
        member.setName(annotation.name()); // 사용자 이름 설정
        member.setNickName(annotation.nickName()); // 사용자 닉네임 설정
        member.setBirthDt(LocalDate.now().minusYears(20L)); // 생일 (20년 전) 설정
        member.setRequiredTerms1(true); // 필수 약관 동의 여부 설정
        member.setRequiredTerms2(true);
        member.setRequiredTerms3(true);
        member.setCredentialChangedAt(LocalDateTime.now()); // 비밀번호 변경 시간 설정

        // 사용자 권한(SimpleGrantedAuthority) 설정: Spring Security에서 사용
        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.authority())
                .map(a -> new SimpleGrantedAuthority(a.name())) // 권한 이름을 SimpleGrantedAuthority로 변환
                .toList();

        // 사용자 권한(Authorities) 설정: 애플리케이션 도메인 엔티티에서 사용
        List<Authorities> _authorities = Arrays.stream(annotation.authority())
                .map(a -> {
                    Authorities auth = new Authorities();
                    auth.setAuthority(a); // 권한 설정
                    auth.setMember(member); // 권한과 사용자 매핑
                    return auth;
                }).toList();
        member.setAuthorities(_authorities); // Member 객체에 권한 설정

        // MemberInfo 객체 생성: 인증 과정에서 사용자 정보를 담는 DTO 역할
        MemberInfo memberInfo = MemberInfo
                .builder()
                .email(annotation.email()) // 이메일 설정
                .password(annotation.password()) // 비밀번호 설정
                .member(member) // Member 엔티티 설정
                .authorities(authorities) // 권한 설정
                .build();

        // UsernamePasswordAuthenticationToken 생성: Spring Security 인증 토큰
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                memberInfo, // Principal (인증된 사용자 정보)
                annotation.password(), // Credentials (비밀번호)
                authorities // Granted Authorities (사용자 권한)
        );

        // SecurityContext 생성 및 인증 토큰 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token); // SecurityContext에 인증 정보 추가

        return context; // 생성된 SecurityContext 반환
    }
}
