package org.koreait.member;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.koreait.member.entities.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * MemberInfo 클래스는 Spring Security의 UserDetails 인터페이스를 구현하여
 * 사용자 인증과 관련된 정보를 제공하는 역할을 합니다.
 */
@Getter // Lombok을 사용하여 모든 필드의 getter 메서드를 자동 생성
@Builder // Lombok을 사용하여 객체를 빌더 패턴으로 생성 가능
@ToString // Lombok을 사용하여 객체를 문자열로 변환하는 메서드 자동 생성
public class MemberInfo implements UserDetails {

    /**
     * 사용자 이메일 주소 (로그인 ID로 사용).
     */
    private String email;

    /**
     * 사용자 비밀번호.
     */
    private String password;

    /**
     * 사용자 권한 목록.
     * Spring Security의 `GrantedAuthority`를 사용하여 권한을 표현.
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Member 엔티티 객체로 추가적인 사용자 정보를 포함합니다.
     */
    private Member member;

    /**
     * 사용자 권한 정보를 반환합니다.
     *
     * @return 사용자 권한의 컬렉션
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 사용자 비밀번호를 반환합니다.
     *
     * @return 비밀번호
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 사용자 이메일(로그인 ID)을 반환합니다.
     *
     * @return 이메일 주소
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환합니다.
     *
     * @return 항상 `true`를 반환하여 계정 만료를 관리하지 않음
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨 있지 않은지 여부를 반환합니다.
     *
     * @return 항상 `true`를 반환하여 계정 잠금 상태를 관리하지 않음
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 인증 정보(비밀번호)가 만료되지 않았는지 여부를 반환합니다.
     * - 비밀번호가 1개월 이상 변경되지 않은 경우 만료로 간주.
     *
     * @return 인증 정보가 만료되지 않았으면 `true`, 그렇지 않으면 `false`
     */
    @Override
    public boolean isCredentialsNonExpired() {
        LocalDateTime credentialChangedAt = member.getCredentialChangedAt();
        return credentialChangedAt != null &&
                credentialChangedAt.isAfter(LocalDateTime.now().minusMonths(1L));
    }

    /**
     * 계정이 활성화되어 있는지 여부를 반환합니다.
     * - 탈퇴한 사용자인 경우 비활성화로 간주.
     *
     * @return 탈퇴하지 않은 경우 `true`, 탈퇴한 경우 `false`
     */
    @Override
    public boolean isEnabled() {
        return member.getDeletedAt() == null;
    }
}
