package com.candidatureformateur.entity;
 
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn; 
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "DOCUMENT_PARTICIPATION_PLANIFIEE",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_participation_document_formateur",
            columnNames = {
                "participation_planifiee_id",
                "document_formateur_id"
            }
        )
    }
)
public class DocumentParticipationPlanifiee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "participation_planifiee_id",
        nullable = false
    )
    private ParticipationPlanifiee participationPlanifiee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "document_demande_id",
        nullable = false
    )
    private DocumentDemande documentDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "document_formateur_id",
        nullable = false
    )
    private DocumentFormateur documentFormateur;
        @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd",
        timezone = "Africa/Tunis"
    )
    @Column(nullable = false)
    private LocalDateTime dateParticipation=LocalDateTime.now(ZoneId.of("Africa/Tunis"));

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd",
        timezone = "Africa/Tunis"
    )
    @Column(nullable = true)
    private LocalDateTime dateDerniereModification;
}