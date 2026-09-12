package com.candidatureformateur.repository;

 

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.Formateur;

import java.time.LocalDate;
import java.util.Optional;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {

    Optional<Formateur> findByCinAndIdUniqAndDateNaissance(String cin, String idUniq, LocalDate dateNaissance);
    boolean existsByCin(String cin); 
    boolean existsByIdUniq(String idUniq);
    
    boolean existsByCinAndIdNot(String cin, Long id); 
    boolean existsByIdUniqAndIdNot(String idUniq, Long id);

    Optional<Formateur> findByRefFormateur(String refFormateur);
}