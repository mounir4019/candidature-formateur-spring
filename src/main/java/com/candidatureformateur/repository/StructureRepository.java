package com.candidatureformateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.Structure;
 import java.util.Optional;
public interface StructureRepository extends JpaRepository<Structure, Long> {

   Optional<Structure> findByCodeStructure(String codeStructure);
}