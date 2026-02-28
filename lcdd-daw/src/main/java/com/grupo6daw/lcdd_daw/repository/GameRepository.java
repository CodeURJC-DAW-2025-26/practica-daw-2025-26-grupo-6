package com.grupo6daw.lcdd_daw.repository;

import java.util.List;
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

    @Query(""" 
	  SELECT g
	  FROM Game g
	  WHERE
	  (LOWER(g.gameName) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL OR :name = '') AND
	  (LOWER(g.genre) = LOWER(:tag) OR :tag IS NULL OR :tag = '') AND
	  ((g.minPlayers <= :players AND g.maxPlayers >= :players) OR :players IS NULL) AND
	  ((g.minDuration <= :duration AND g.maxDuration >= :duration) OR :duration IS NULL)
    """)
      Page<Game> findByNameAndTagAndPlayersAndDuration(String name, String tag, Integer players, Integer duration, Pageable page);
}
