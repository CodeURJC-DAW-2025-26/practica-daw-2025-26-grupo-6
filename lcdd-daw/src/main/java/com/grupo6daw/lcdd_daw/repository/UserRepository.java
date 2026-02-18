package com.grupo6daw.lcdd_daw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6daw.lcdd_daw.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

