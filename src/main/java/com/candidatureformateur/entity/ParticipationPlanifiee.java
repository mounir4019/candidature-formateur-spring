package com.candidatureformateur.entity;
import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@Entity
@Table(
    name = "PARTICIPATION_PLANIFIEE",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_formateur_candidature",
            columnNames = {
                "formateur_id",
                "candidature_planifiee_id"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({
    "hibernateLazyInitializer",
    "handler"
})
public class ParticipationPlanifiee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Africa/Tunis" )
    private LocalDate dateSoumission = LocalDate.now(ZoneId.of("Africa/Tunis"));

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
   // @JsonIgnore
    @JsonIgnoreProperties({
    "hibernateLazyInitializer",
    "handler"
})
    private Formateur formateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidature_planifiee_id", nullable = false)
    @JsonIgnore
    @JsonIgnoreProperties({  
    "hibernateLazyInitializer",
    "handler"
    })
    private CandidaturePlanifiee candidaturePlanifiee;
}