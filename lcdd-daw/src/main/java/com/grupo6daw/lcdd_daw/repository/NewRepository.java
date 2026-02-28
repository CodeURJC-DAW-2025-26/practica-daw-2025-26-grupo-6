package com.grupo6daw.lcdd_daw.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6daw.lcdd_daw.model.New;

public interface NewRepository extends JpaRepository<New, Long> {

	@Query("""
	SELECT n
	FROM New n
	WHERE
	(LOWER(n.newName) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL OR :name = '') AND
	(LOWER(n.newTag) = LOWER(:tag) OR :tag IS NULL OR :tag = '')
	""")
	Page<New> findByNameAndTag(String name, String tag, Pageable page);

}

