package com.grupo6daw.lcdd_daw.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6daw.lcdd_daw.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByGameName(String gameName);

    @Query("""
      SELECT g AS game, COUNT(DISTINCT u) AS favCount
      FROM Game g
      LEFT JOIN g.favoritedByUsers u
      GROUP BY g
      ORDER BY COUNT(DISTINCT u) DESC, g.gameName ASC
    """)
    Page<Object[]> findGamesOrderedByFavs(Pageable pageable);

}
