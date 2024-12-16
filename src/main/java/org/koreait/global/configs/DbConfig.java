package org.koreait.global.configs;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DbConfig 클래스는 데이터베이스 관련 설정을 관리합니다.
 * 여기서는 QueryDSL을 사용하기 위해 JPAQueryFactory Bean을 정의합니다.
 */
@Configuration // Spring 설정 클래스임을 나타냅니다.
public class DbConfig {

    /**
     * JPA의 EntityManager를 주입받습니다.
     * EntityManager는 JPA에서 엔티티의 생명 주기를 관리하고, 데이터베이스와의 상호작용을 담당합니다.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * JPAQueryFactory Bean을 생성합니다.
     * QueryDSL을 활용하여 동적 쿼리를 생성하고 실행할 때 사용됩니다.
     *
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // EntityManager를 사용하여 JPAQueryFactory 인스턴스를 생성
        return new JPAQueryFactory(em);
    }
}
