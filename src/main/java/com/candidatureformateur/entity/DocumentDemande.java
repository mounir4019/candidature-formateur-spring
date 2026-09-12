package com.candidatureformateur.entity; 
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DOCUMENT_DEMANDE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"candidaturePlanifiee"})
public class DocumentDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* @Column(nullable = false)
    private String libelle; */

    @Column(nullable = false)
    private boolean obligatoire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidature_planifiee_id", nullable = false)
    private CandidaturePlanifiee candidaturePlanifiee;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_document_id", nullable = false)
    private TypeDocument typeDocument;
}