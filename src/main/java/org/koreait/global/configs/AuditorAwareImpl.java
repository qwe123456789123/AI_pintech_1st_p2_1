package org.koreait.global.configs;

// 필요한 클래스와 애너테이션 임포트
import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * AuditorAwareImpl 클래스는 JPA Auditing 기능에서 현재 사용자를 제공하기 위한 구현체입니다.
 * 엔티티 생성 또는 수정 시, 해당 작업을 수행한 사용자의 정보를 자동으로 기록합니다.
 */
@Lazy // 이 컴포넌트는 실제로 사용될 때까지 초기화되지 않습니다.
@Component // Spring이 관리하는 Bean으로 등록
@RequiredArgsConstructor // `final` 필드의 의존성을 자동으로 주입해주는 Lombok 애너테이션
public class AuditorAwareImpl implements AuditorAware<String> {
// AuditorAwareImpl 에 반환값을 저장함
    // 현재 로그인 상태 및 사용자 정보를 확인하기 위해 사용
    private final MemberUtil memberUtil;

    /**
     * 현재 작업을 수행한 사용자의 이메일 주소를 반환합니다.
     * - 사용자가 로그인 상태인 경우 MemberUtil을 통해 이메일을 가져옵니다.
     * - 로그아웃 상태라면 null을 반환합니다.
     *
     * @return 현재 사용자의 이메일 주소(Optional 형식)
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        String email = null;

        // MemberUtil을 사용하여 사용자가 로그인되어 있는지 확인
        if (memberUtil.isLogin()) {
            // 로그인된 사용자의 이메일 주소를 가져옴
            email = memberUtil.getMember().getEmail();
        }

        // Optional.ofNullable을 사용하여 null 값을 허용
        return Optional.ofNullable(email);
    }
}
