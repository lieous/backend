package com.diploma.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_data", columnDefinition = "jsonb")
    private Map<String, Object> profileData = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", columnDefinition = "jsonb")
    private Map<String, Object> settings = new HashMap<>();

    @Column(name = "created_at")
    private Long createdAt = System.currentTimeMillis();

    @Column(name = "password_hash")
    private String passwordHash;
}