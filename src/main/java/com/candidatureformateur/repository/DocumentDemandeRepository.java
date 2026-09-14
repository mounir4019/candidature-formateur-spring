package com.candidatureformateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.DocumentDemande; 
 
public interface DocumentDemandeRepository extends JpaRepository<DocumentDemande, Long> {

  boolean existsByCandidaturePlanifieeIdAndTypeDocumentId(  Long idCandidaturePlanifiee,   Long idTypeDocument );
  boolean existsByCandidaturePlanifieeIdAndTypeDocumentIdAndIdNot( Long idCandidaturePlanifiee,  Long idTypeDocument,  Long idDocumentDemande );   
  boolean existsByTypeDocumentId(Long idTypeDocument);   
}