package org.koreait.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * LoginSuccessHandler 클래스는 Spring Security 로그인 성공 시 동작을 정의합니다.
 * - 로그인 성공 후 페이지 이동 로직을 처리합니다.
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 로그인 성공 시 실행되는 메서드.
     * - 세션 정리 및 로그인 성공 후 리다이렉트를 처리합니다.
     *
     * @param request        클라이언트 요청 객체
     * @param response       서버 응답 객체
     * @param authentication 인증 성공 정보를 담고 있는 객체
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        // 세션에서 로그인 실패 시 저장했던 `requestLogin` 정보를 제거
        session.removeAttribute("requestLogin");

        /**
         * Authentication 객체에서 로그인 사용자 정보 추출 가능:
         * - `authentication.getPrincipal()`을 통해 `UserDetails` 구현체를 가져올 수 있습니다.
         * - 아래 코드는 주석 처리되어 있으며, 필요 시 활성화 가능.
         */
        // MemberInfo memberInfo = (MemberInfo) authentication.getPrincipal();
        // System.out.println(memberInfo);

        /**
         * 로그인 성공 시 이동할 페이지 결정:
         * 1) `redirectUrl` 파라미터가 존재하면 해당 URL로 이동.
         * 2) `redirectUrl`이 없으면 기본적으로 메인 페이지("/")로 이동.
         */
        String redirectUrl = request.getParameter("redirectUrl");
        redirectUrl = StringUtils.hasText(redirectUrl) ? redirectUrl : "/";

        // 클라이언트 요청의 컨텍스트 경로를 기반으로 리다이렉트
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
