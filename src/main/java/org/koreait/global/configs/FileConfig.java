package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * FileConfig 클래스는 파일 관련 설정을 관리합니다.
 * - 파일 URL 매핑과 실제 파일 경로를 설정하여 정적 파일을 서빙할 수 있도록 설정합니다.
 */
@Configuration // Spring 설정 클래스임을 나타냅니다.
@RequiredArgsConstructor // `final` 필드의 의존성을 자동 주입하도록 설정
@EnableConfigurationProperties(FileProperties.class) // FileProperties 클래스를 @ConfigurationProperties로 활성화
public class FileConfig implements WebMvcConfigurer {
// 스프링 기본 설정을 해야 하는 경우 WebMvcConfigurers 를 추가해야함
    // 파일 설정 속성을 주입받습니다.
    // 정적 경로 이다
    private final FileProperties properties;

    /**
     * 리소스 핸들러를 설정합니다.
     * - 클라이언트에서 접근 가능한 URL과 서버의 실제 파일 시스템 경로를 매핑합니다.
     *
     * @param registry 리소스 핸들러를 등록하기 위한 객체
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 클라이언트에서 접근하는 URL 패턴 설정
        registry.addResourceHandler(properties.getUrl() + "**")
                // 실제 파일이 저장된 로컬 디렉토리 경로 설정
                .addResourceLocations("file:///" + properties.getPath());
    }
}
