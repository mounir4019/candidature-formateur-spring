package com.candidatureformateur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.CandidatureSpontanee;
 
public interface CandidatureSpontaneeRepository extends JpaRepository<CandidatureSpontanee, Long> {
 
      List<CandidatureSpontanee> findByFormateurId(Long idFormateur);
      boolean existsByFormateurIdAndThemeFormationId(  Long idFormateur, Long idTheme);

      List<CandidatureSpontanee> findByThemeFormationId(Long idTheme);
      boolean existsByThemeFormationId(Long idTheme);
}