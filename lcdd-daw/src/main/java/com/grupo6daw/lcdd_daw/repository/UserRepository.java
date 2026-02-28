package com.grupo6daw.lcdd_daw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserNickname(String nickname);
    boolean existsByUserEmail(String email);

    @Query("SELECT u.userImage from UserTable u WHERE u.userId = :id")
    Optional<Image> findImageByUserId(long id);
}