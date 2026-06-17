package com.diploma.backend.domain.repository;

import com.diploma.backend.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    
    Optional<UserEntity> findByEmail(String email);
}