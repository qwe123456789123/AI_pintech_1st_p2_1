package org.koreait.global.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FileProperties 클래스는 파일 업로드 관련 설정 값을 관리합니다.
 * - 외부 설정 파일(application.properties 또는 application.yml)에서 값을 주입받습니다.
 * - prefix `file.upload`에 해당하는 속성을 매핑합니다.
 */
@Data // Lombok을 사용하여 getter, setter, toString, equals, hashCode 메서드 자동 생성
@ConfigurationProperties(prefix = "file.upload") // 설정 파일에서 `file.upload`로 시작하는 속성을 매핑
public class FileProperties {

    /**
     * 파일이 저장될 경로.
     * 설정 파일에서 `file.upload.path` 속성에 해당하는 값을 주입받습니다.
     */
    private String path;

    /**
     * 클라이언트가 접근할 파일 URL.
     * 설정 파일에서 `file.upload.url` 속성에 해당하는 값을 주입받습니다.
     */
    private String url;
}
