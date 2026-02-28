package com.grupo6daw.lcdd_daw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6daw.lcdd_daw.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserNickname(String nickname);
    boolean existsByUserEmail(String email);
}

