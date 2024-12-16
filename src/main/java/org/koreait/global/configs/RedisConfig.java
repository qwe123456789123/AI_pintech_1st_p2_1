package org.koreait.global.configs;

// 필요한 Spring 및 Redis 관련 클래스 임포트
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig 클래스는 Redis와 관련된 설정을 정의합니다.
 * Redis 연결, 데이터 직렬화 방식, 트랜잭션 지원 등을 설정합니다.
 */
@Configuration // Spring 설정 클래스임을 나타냅니다.
public class RedisConfig {

    /**
     * Redis 서버의 호스트 주소를 주입받습니다.
     * 이 값은 `application.properties` 또는 `application.yml` 파일에서 정의된
     * `spring.data.redis.host` 속성으로부터 읽어옵니다.
     */
    @Value("${spring.data.redis.host}")
    private String host;

    /**
     * Redis 서버의 포트 번호를 주입받습니다.
     * 이 값은 `application.properties` 또는 `application.yml` 파일에서 정의된
     * `spring.data.redis.port` 속성으로부터 읽어옵니다.
     */
    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * RedisConnectionFactory를 생성하여 Redis 서버와의 연결을 관리합니다.
     * 여기서는 Lettuce 라이브러리를 사용하여 Redis 연결을 생성합니다.
     *
     * @return RedisConnectionFactory 객체
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // LettuceConnectionFactory를 생성하며, Redis 서버의 호스트와 포트를 설정합니다.
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * RedisTemplate을 생성하여 Redis에 데이터를 읽고 쓰는 작업을 수행합니다.
     * - 키와 값을 String으로 직렬화하여 저장합니다.
     * - 트랜잭션을 지원합니다.
     *
     * @return RedisTemplate 객체
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        // RedisTemplate 인스턴스 생성
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();

        // Redis 연결 팩토리를 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // RedisTemplate에서 트랜잭션 지원 활성화
        redisTemplate.setEnableTransactionSupport(true);

        // Redis 키와 값을 String으로 직렬화하도록 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Redis 해시 키와 해시 값을 String으로 직렬화하도록 설정
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
