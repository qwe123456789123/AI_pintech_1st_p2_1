package org.koreait.member.repositories;

import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.AuthoritiesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AuthoritiesRepository extends JpaRepository<Authorities, AuthoritiesId>, QuerydslPredicateExecutor<Authorities> {
}

// Authorities: 관리할 엔터티 클래스
// AuthoritiesId: 엔터티의 복합 기본 키 클래스
// Authorities 엔터티는 사용자와 권한 간의 관계를 관리하며, AuthoritiesId를 통해 복합 키를 처리함
