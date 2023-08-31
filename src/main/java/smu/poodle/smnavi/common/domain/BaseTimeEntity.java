package smu.poodle.smnavi.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    public String getCreatedDateToString() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getCreatedTimeToString() {
        return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}