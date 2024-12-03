package org.koreait.member.repositories;

import org.koreait.member.entities.Member;
import org.koreait.member.entities.QMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public  interface MemberRepository extends JpaRepository<Member,Long>, QuerydslPredicateExecutor<Member> {
    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findByEmail(String email);

//save(Member entity): 엔터티 저장.
//findById(Long id): 기본 키로 엔터티 조회.
//findAll(): 모든 엔터티 조회.
//deleteById(Long id): 기본 키로 엔터티 삭제.

    default boolean exists (String email){
        QMember member = QMember.member;
        return exists(member.email.eq(email));
    }
}

// authorities - @OneToMany 관계로, Member와 Authority 엔터티가 연결되어 있음