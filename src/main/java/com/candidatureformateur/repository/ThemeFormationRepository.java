package com.candidatureformateur.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.candidatureformateur.entity.ThemeFormation;
 

public interface ThemeFormationRepository extends JpaRepository<ThemeFormation, Long> {
   Optional<ThemeFormation> findById(Long id);
   @Query("""
    SELECT t
    FROM ThemeFormation t
    WHERE t.id NOT IN (
        SELECT c.themeFormation.id
        FROM CandidatureSpontanee c
        WHERE c.formateur.id = :idFormateur
    )
    """)
    List<ThemeFormation> findThemesNonInscritsByFormateur( @Param("idFormateur") Long idFormateur);  
}