package org.koreait.email.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.koreait.email.controllers.RequestEmail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EmailService는 이메일 발송을 처리하는 서비스입니다.
 * - 템플릿을 기반으로 동적 이메일 콘텐츠 생성 및 발송.
 */
@Service
@RequiredArgsConstructor // `final` 필드의 의존성을 자동으로 주입
public class EmailService {

    private final JavaMailSender javaMailSender; // 이메일 발송을 처리하는 JavaMailSender
    private final SpringTemplateEngine templateEngine; // Thymeleaf 템플릿 엔진

    /**
     * 이메일 발송 처리
     *
     * @param form    이메일 발송 요청 데이터를 담은 객체
     * @param tpl     템플릿 코드 (email/{tpl}.html)
     * @param tplData 템플릿에 전달할 데이터 (EL 속성으로 추가됨)
     * @return 이메일 발송 성공 여부
     */
    public boolean sendEmail(RequestEmail form, String tpl, Map<String, Object> tplData) {
        try {
            // 템플릿 데이터 초기화
            Context context = new Context();
            tplData = Objects.requireNonNullElseGet(tplData, HashMap::new);

            // 이메일 관련 정보 설정
            List<String> to = form.getTo(); // 수신자
            List<String> cc = form.getCc(); // 참조
            List<String> bcc = form.getBcc(); // 숨은 참조
            String subject = form.getSubject(); // 제목
            String content = form.getContent(); // 내용

            // 템플릿 데이터에 이메일 정보 추가
            tplData.put("to", to);
            tplData.put("cc", cc);
            tplData.put("bcc", bcc);
            tplData.put("subject", subject);
            tplData.put("content", content);

            // 템플릿 변수 설정
            context.setVariables(tplData);

            // 템플릿 처리 및 HTML 콘텐츠 생성
            String html = templateEngine.process("email/" + tpl, context);

            // 이메일 메시지 생성
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            // 수신자 설정
            helper.setTo(to.toArray(String[]::new));
            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.toArray(String[]::new)); // 참조 설정
            }

            if (bcc != null && !bcc.isEmpty()) {
                helper.setBcc(bcc.toArray(String[]::new)); // 숨은 참조 설정
            }

            helper.setSubject(subject); // 제목 설정
            helper.setText(html, true);
            // HTML 콘텐츠 설정, 번역된 데이터를 Html에 설정함

            // 이메일 발송
            javaMailSender.send(message);

            return true; // 이메일 발송 성공
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 로그 출력
        }

        return false; // 이메일 발송 실패
    }
}
