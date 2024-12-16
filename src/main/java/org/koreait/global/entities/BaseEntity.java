package org.koreait.global.entities;

// 필요한 클래스들을 임포트
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseEntity는 엔티티 공통 속성과 로직을 제공하는 추상 클래스입니다.
 * 모든 엔티티에서 상속받아 생성, 수정, 삭제 일시를 자동 관리할 수 있습니다.
 */
@Data // Lombok을 사용하여 getter, setter, toString 등 메서드를 자동 생성
@MappedSuperclass // 이 클래스의 필드를 상속받는 엔티티의 필드로 매핑
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능을 사용하여 자동으로 생성 및 수정 시간을 관리
public abstract class BaseEntity {

    /**
     * 생성일시: 엔티티가 처음 생성된 시간.
     * @CreatedDate 애너테이션으로 엔티티가 저장될 때 자동으로 값이 설정됩니다.
     */
    @CreatedDate
    @Column(updatable = false) // 생성일시는 수정되지 않도록 설정
    private LocalDateTime createdAt; // 등록일시

    /**
     * 수정일시: 엔티티가 마지막으로 수정된 시간.
     * @LastModifiedDate 애너테이션으로 엔티티가 수정될 때 자동으로 값이 업데이트됩니다.
     */
    @LastModifiedDate
    @Column(insertable = false) // 수정일시는 초기 삽입(insert) 시 값을 설정하지 않도록 설정
    private LocalDateTime modifiedAt; // 수정일시

    /**
     * 삭제일시: 엔티티가 삭제된 시간.
     * 이 값은 수동으로 설정되어 논리적 삭제(soft delete)를 구현하는 데 사용됩니다.
     */
    private LocalDateTime deletedAt; // 삭제일시
}
