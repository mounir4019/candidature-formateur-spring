package com.candidatureformateur.repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.CandidaturePlanifiee;
import com.candidatureformateur.entity.Structure;
 
public interface CandidaturePlanifieeRepository  extends JpaRepository<CandidaturePlanifiee, Long> {

     List<CandidaturePlanifiee> findByApprouveeTrueAndDdLessThanEqualAndDfGreaterThanEqual(
        LocalDate dateDebut,
        LocalDate dateFin
        );

   Optional< CandidaturePlanifiee> findByRefCandidaturePlanifiee(String refCP);

       Page<CandidaturePlanifiee>
    findByStructureAndDateCreationGreaterThanEqualOrderByDateCreationDesc(
            Structure structure,
            LocalDate date,
            Pageable pageable
    );
    boolean existsByThemeFormationId(Long idTheme);

}