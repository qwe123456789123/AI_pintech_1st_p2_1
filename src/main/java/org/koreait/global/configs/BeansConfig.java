package org.koreait.global.configs;

// 필요한 클래스 임포트
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

/**
 * BeansConfig 클래스는 애플리케이션에서 자주 사용되는 Bean들을 정의하는 설정 클래스입니다.
 * - RestTemplate: REST API 호출을 위한 클라이언트
 * - ModelMapper: 객체 간의 매핑 도구
 * - ObjectMapper: JSON 데이터를 처리하기 위한 Jackson 라이브러리 객체
 */
@Configuration // Spring 설정 클래스임을 나타냄
public class BeansConfig {

    /**
     * RestTemplate Bean을 생성합니다.
     * - REST API 호출을 위한 클라이언트로 사용됩니다.
     * - @Lazy: 이 Bean은 실제로 필요할 때까지 초기화되지 않습니다.
     *
     * @return RestTemplate 인스턴스
     */
    @Lazy
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * ModelMapper Bean을 생성합니다.
     * - 객체 간의 매핑을 쉽게 처리할 수 있도록 도와줍니다.
     * - STRICT 매칭 전략을 사용하여 이름이 정확히 일치하는 필드만 매핑합니다.
     * - @Lazy: 이 Bean은 실제로 필요할 때까지 초기화되지 않습니다.
     *
     * @return ModelMapper 인스턴스
     *
     */
    @Lazy
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // 매칭 전략을 STRICT로 설정하여 필드 이름이 정확히 일치해야 매핑이 이루어지도록 설정
        // 위의 설명을 하기 위해서 class class를 사용함
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }

    /**
     * ObjectMapper Bean을 생성합니다.
     * - JSON 데이터를 Java 객체로 변환하거나 그 반대로 변환할 때 사용됩니다.
     * - JavaTimeModule을 등록하여 Java 8의 날짜 및 시간 API(`java.time`)를 처리할 수 있도록 설정합니다.
     * - @Lazy: 이 Bean은 실제로 필요할 때까지 초기화되지 않습니다.
     *
     * @return ObjectMapper 인스턴스
     */
    @Lazy
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();

        // JavaTimeModule을 등록하여 LocalDate, LocalDateTime 등의 Java 8 시간 API를 처리 가능하도록 설정
        om.registerModule(new JavaTimeModule());

        return om;
    }
}
