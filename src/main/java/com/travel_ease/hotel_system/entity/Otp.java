package com.travel_ease.hotel_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Otp {
    @Id
    @Column(name = "property_id", length = 80, nullable = false)
    private UUID id;

    @Column(name = "code", length = 80, nullable = false)
    private String code;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "attempts")
    private Integer attempts;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
