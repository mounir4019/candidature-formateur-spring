package com.candidatureformateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.DocumentParticipationPlanifiee;

public interface DocumentParticipationPlanifieeRepository
        extends JpaRepository<DocumentParticipationPlanifiee, Long> {

    boolean existsByDocumentFormateurId(Long documentFormateurId);
    boolean existsByDocumentFormateur_Id(Long documentFormateurId);
}