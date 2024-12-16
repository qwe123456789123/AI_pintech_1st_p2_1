package org.koreait.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.koreait.member.controllers.RequestLogin;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LoginFailureHandler 클래스는 Spring Security 로그인 실패 시 동작을 정의합니다.
 * - 다양한 인증 실패 상황에 맞는 처리를 수행합니다.
 * - 실패 원인에 따라 적절한 메시지를 설정하고 로그인 페이지로 리다이렉트합니다.
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {

    /**
     * 로그인 실패 시 실행되는 메서드.
     * - 실패 원인에 따라 사용자에게 적절한 피드백을 제공합니다.
     *
     * @param request  클라이언트 요청 객체
     * @param response 서버 응답 객체
     * @param exception 인증 실패 원인을 담고 있는 예외 객체
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpSession session = request.getSession();
        // 세션에서 RequestLogin 객체를 가져오거나 없으면 새로운 객체 생성
        RequestLogin form = Objects.requireNonNullElse((RequestLogin) session.getAttribute("requestLogin"), new RequestLogin());
        form.setErrorCodes(null); // 이전 에러 코드를 초기화
        // 요청에서 이메일과 비밀번호를 가져와 RequestLogin 객체에 설정
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        form.setEmail(email);
        form.setPassword(password);

        // 기본 리다이렉트 URL 설정
        String redirectUrl = request.getContextPath() + "/member/login";

        // 로그인 실패 원인에 따른 처리
        if (exception instanceof BadCredentialsException) {
            // 아이디 또는 비밀번호 불일치 또는 입력값 부족
            List<String> errorCodes = Objects.requireNonNullElse(form.getErrorCodes(), new ArrayList<>());

            if (!StringUtils.hasText(email)) {
                errorCodes.add("NotBlank_email"); // 이메일이 비어있음
            }

            if (!StringUtils.hasText(password)) {
                errorCodes.add("NotBlank_password"); // 비밀번호가 비어있음
            }

            if (errorCodes.isEmpty()) {
                errorCodes.add("Failure.validate.login"); // 유효성 검사 실패
                // 아이디와 비번이 일치 하지 않을때 나옴
            }

            form.setErrorCodes(errorCodes);
        } else if (exception instanceof CredentialsExpiredException) {
            // 비밀번호가 만료된 경우 아래 주소로 이동하여 변경하도록 함
            redirectUrl = request.getContextPath() + "/member/password/change";
        } else if (exception instanceof DisabledException) {
            // 탈퇴한 회원인 경우
            form.setErrorCodes(List.of("Failure.disabled.login"));
        }

        // 실패 정보를 세션에 저장
        session.setAttribute("requestLogin", form);

        // 로그인 페이지로 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}
