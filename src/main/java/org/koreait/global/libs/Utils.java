package org.koreait.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Utils {

    private final HttpServletRequest request;
    private final MessageSource messageSource;

    public boolean isMobile() {

        // 요청 헤더 - User-Agent / 브라우저 정보
        String ua = request.getHeader("User-Agent");
        String pattern = ".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*";


        return ua.matches(pattern);
    }

    /**
     * mobile, front 템플릿 분리 함수
     *
     * @param path
     * @return
     */
    public String tpl(String path) {
        String prefix = isMobile() ? "mobile" : "front";

        return String.format("%s/%s", prefix, path);
    }

    /**
     * 메서지 코드로 조회된 문구
     *
     * @param code
     * @return
     */
    public String getMessage(String code) {
        Locale lo = request.getLocale(); // 사용자 요청 헤더(Accept-Language)

        return messageSource.getMessage(code, null, lo);
    }

    public List<String> getMessages(String[] codes) {

            return Arrays.stream(codes).map(c -> {
                try {
                    return getMessage(c);
                } catch (Exception e) {
                    return "";
                }
            }).filter(s -> !s.isBlank()).toList();

    }

    /**
     * REST 커맨드 객체 검증 실패시에 에러 코드를 가지고 메세지 추출
     *
     * @param errors
     * @return
     */
    public Map<String, List<String>> getErrorMessages(Errors errors) {
        // 메시지 소스(MessageSource)를 ResourceBundleMessageSource로 캐스팅
        // 메시지 처리를 담당
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;
        // 메시지 코드를 기본 메시지로 사용하는 설정을 비활성화
        // 코드가 메시지로 사용되는 것을 방지하고, 실제 메시지를 찾도록 설정
        ms.setUseCodeAsDefaultMessage(false);

        try {
            // 필드별 에러 메시지를 수집하여 Map으로 저장
            Map<String, List<String>> messages = errors.getFieldErrors()
                    .stream() // 필드 에러 리스트를 스트림으로 변환
                    .collect(Collectors.toMap(
                            FieldError::getField,// Key: 필드 이름
                            f -> getMessages(f.getCodes()), // Value: 필드와 연관된 에러 메시지 리스트
                            (v1, v2) -> v2// Key 충돌 시, 기존 값(v1)을 대체할 값(v2)을 사용
                    ));

            // 글로벌 에러 메시지(Global Error)를 수집하여 리스트로 저장
            List<String> gMessages = errors.getGlobalErrors()
                    .stream() // 글로벌 에러 리스트를 스트림으로 변환
                    .flatMap(o -> getMessages(o.getCodes()).stream()) // 각 글로벌 에러의 코드로 메시지를 가져옴
                    .toList(); // 결과를 List로 변환

            // 글로벌 에러 메시지가 존재하는 경우
            if (!gMessages.isEmpty()) {
                // "global" 키로 글로벌 에러 메시지 리스트를 Map에 추가
                messages.put("global", gMessages);
            }

            // 최종적으로 구성된 메시지 Map 반환
            return messages;
        } finally {
            // finally 사용한 이유: finally 무조권 실행 되므로 마지막에 원상태로 복원하기 위해서 사용함
            // 메시지 소스의 기본 설정 복원
            // 싱글톤 객체이므로 설정 변경이 다른 코드에 영향을 미치지 않도록 복원
            ms.setUseCodeAsDefaultMessage(true);
        }
    }
}
