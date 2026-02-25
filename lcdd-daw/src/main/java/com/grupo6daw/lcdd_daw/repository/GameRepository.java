package com.grupo6daw.lcdd_daw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6daw.lcdd_daw.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameName(String gameName);

}

