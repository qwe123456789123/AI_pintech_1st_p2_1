package org.koreait.global.advices;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.exceptions.CommonException;
import org.koreait.global.exceptions.scripts.AlertBackException;
import org.koreait.global.exceptions.scripts.AlertException;
import org.koreait.global.exceptions.scripts.AlertRedirectException;
import org.koreait.global.libs.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * CommonControllerAdvice는 컨트롤러 전역에서 발생하는 예외를 처리합니다.
 * - @ControllerAdvice: 컨트롤러에 대한 전역 예외 처리 적용.
 * - @ApplyErrorPage 애너테이션이 있는 컨트롤러에만 적용됩니다.
 */
@ControllerAdvice(annotations = ApplyErrorPage.class)
@RequiredArgsConstructor // `final` 필드의 의존성을 자동 주입
public class CommonControllerAdvice {

    private final Utils utils; // 메시지 유틸리티 클래스

    /**
     * 모든 예외를 처리하는 핸들러 메서드.
     * - 예외 유형에 따라 적절한 템플릿 및 데이터 반환.
     *
     * @param e       발생한 예외
     * @param request 현재 HTTP 요청 정보
     * @return ModelAndView 객체 (예외 정보 및 템플릿 설정 포함)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView errorHandler(Exception e, HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();

        // 기본 응답 상태 코드 및 템플릿 설정
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 응답 코드 500
        String tpl = "error/error"; // 기본 출력 템플릿
        String message = e.getMessage();

        // 요청 메타데이터 저장
        data.put("method", request.getMethod());
        data.put("path", request.getContextPath() + request.getRequestURI());
        data.put("querystring", request.getQueryString());
        data.put("exception", e);

        // CommonException 및 하위 클래스에 대한 처리
        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();
            message = commonException.isErrorCode() ? utils.getMessage(message) : message;

            // 스크립트 기반 응답 처리
            StringBuffer sb = new StringBuffer(2048);

            if (e instanceof AlertException) {
                tpl = "common/_execute_script";
                sb.append(String.format("alert('%s');", message));
            }

            if (e instanceof AlertBackException backException) {
                String target = backException.getTarget();
                sb.append(String.format("%s.history.back();", target));
            }

            if (e instanceof AlertRedirectException redirectException) {
                String target = redirectException.getTarget();
                String url = redirectException.getUrl();
                sb.append(String.format("%s.location.replace('%s');", target, url));
            }

            if (!sb.isEmpty()) {
                data.put("script", sb.toString());
            }
        }

        // 예외 처리 데이터를 ModelAndView에 설정
        data.put("status", status.value());
        data.put("_status", status);
        data.put("message", message);

        ModelAndView mv = new ModelAndView();
        mv.setStatus(status);
        mv.addAllObjects(data);
        mv.setViewName(tpl);

        return mv;
    }
}
