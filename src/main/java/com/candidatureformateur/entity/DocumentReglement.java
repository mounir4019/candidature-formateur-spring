package com.candidatureformateur.entity; 

import com.candidatureformateur.servicesDivers.ServiceDivers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne; 
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DOCUMENT_REGLEMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"fichier", "candidaturePlanifiee"})
public class DocumentReglement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;
 

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] fichier;
    @Column(nullable = false, unique = true)
    private String refDocumentReglement=ServiceDivers.generateRef("DR");
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidature_planifiee_id", nullable = false)
    private CandidaturePlanifiee candidaturePlanifiee;
}