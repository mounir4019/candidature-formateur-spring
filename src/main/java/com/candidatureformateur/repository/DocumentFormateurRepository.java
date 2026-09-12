package com.candidatureformateur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.candidatureformateur.entity.DocumentFormateur; 
 
public interface DocumentFormateurRepository extends JpaRepository<DocumentFormateur, Long> {

    long countByFormateurIdAndTypeDocumentId(Long idFormateur, Long idTypeDocument);
    @Query ("SELECT d.fichier FROM DocumentFormateur d WHERE d.formateur.id = :idFormateur")
    List<byte[]> findFichiersByFormateurId(
        @Param("idFormateur") Long idFormateur
);
}