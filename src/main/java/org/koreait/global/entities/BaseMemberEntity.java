package org.koreait.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.koreait.global.configs.AuditorAwareImpl;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass // 공통 이여도 상우클래스임을 알려줌
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseMemberEntity extends BaseEntity {
    @CreatedBy
    @Column(length = 65, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(length = 65, insertable = false)
    private String modifiedBy;
}
