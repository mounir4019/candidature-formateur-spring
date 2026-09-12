package com.candidatureformateur.entity;
import java.time.LocalDate;
import java.time.ZoneId; 

import com.candidatureformateur.servicesDivers.ServiceDivers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "CANDIDATURE_SPONTANEE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CandidatureSpontanee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String refCandidatureSpontanee =ServiceDivers.generateRef("CS");

    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Africa/Tunis" )
    private LocalDate dateSoumission = LocalDate.now(ZoneId.of("Africa/Tunis"));

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false) 
   // @JsonIgnoreProperties({"documentsFormateur", "hibernateLazyInitializer", "handler"})
    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
        })
    private Formateur formateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_formation_id", nullable = false)
    private ThemeFormation themeFormation;
}