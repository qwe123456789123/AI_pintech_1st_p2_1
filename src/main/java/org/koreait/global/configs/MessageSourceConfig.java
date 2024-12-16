package org.koreait.global.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.addBasenames("messages.commons", "messages.validations", "messages.errors", "messages.pokemon");
        // resources.messages 에 문구를 설정하고 출력되어 나오는 문구와 설정한 문구가 일치 하면 설정한 문구로 대체한다.
        ms.setDefaultEncoding("UTF-8");
        // 한국어로 작성하면 문자가 깨지기 때문에 그걸 방치 하기 위해서 사용
        ms.setUseCodeAsDefaultMessage(true);

        return ms;
    }
}
