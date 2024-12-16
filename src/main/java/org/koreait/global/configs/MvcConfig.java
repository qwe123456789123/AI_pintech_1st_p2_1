package org.koreait.global.configs;

// Spring Framework의 주요 기능을 활용하기 위해 필요한 클래스들 임포트
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MvcConfig는 애플리케이션의 웹 MVC 설정을 담당하는 클래스입니다.
 * 주요 기능:
 * - JPA Auditing 활성화
 * - 스케줄링 활성화
 * - Redis HTTP 세션 활성화
 * - 정적 리소스 핸들러 설정
 * - HTTP 메서드 확장 필터 추가
 */
@Configuration // 이 클래스가 Spring 설정 클래스임을 나타냄
@EnableJpaAuditing // JPA Auditing 기능 활성화 (엔티티의 생성 및 수정 시간 자동 관리)
@EnableScheduling // 스케줄링 작업 활성화 (@Scheduled 사용 가능)
@EnableRedisHttpSession // Redis 기반 HTTP 세션 사용 설정
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 정적 리소스 경로를 설정합니다. CSS, JavaScript, 이미지 파일 등을 클라이언트가 요청할 때
     * 지정된 경로에서 파일을 서빙합니다.
     *
     * @param registry 리소스 핸들러를 등록하기 위한 객체
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // 모든 요청 경로에 대해
                .addResourceLocations("classpath:/static/"); // static 디렉토리의 리소스를 사용
    }

    /**
     * HiddenHttpMethodFilter를 Bean으로 등록합니다. 이 필터는 HTML 폼에서 숨겨진 입력 필드를 통해
     * HTTP PATCH, PUT, DELETE 메서드를 사용할 수 있도록 변환해줍니다.
     * 예를 들어, 아래와 같은 폼을 전송하면:
     * <form method='POST' ...>
     *      <input type='hidden' name='_method' value='PATCH'>
     * </form>
     * 실제로는 PATCH 메서드로 처리됩니다.
     *
     * @return HiddenHttpMethodFilter 인스턴스
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter(); // POST 요청을 PATCH, PUT, DELETE로 변환
    }
}
