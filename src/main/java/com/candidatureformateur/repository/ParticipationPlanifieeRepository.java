package com.candidatureformateur.repository;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.ParticipationPlanifiee;
 
public interface ParticipationPlanifieeRepository  extends JpaRepository<ParticipationPlanifiee, Long> {

  boolean existsByFormateurIdAndCandidaturePlanifieeId(  Long formateurId,  Long candidaturePlanifieeId );
  List<ParticipationPlanifiee> findTop10ByFormateurIdOrderByDateSoumissionDesc(  Long formateurId );
  List<ParticipationPlanifiee> findByCandidaturePlanifieeRefCandidaturePlanifiee( String refCp );
  
}