package com.candidatureformateur.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.candidatureformateur.enums.CategorieFormateur;
import com.candidatureformateur.servicesDivers.ServiceDivers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; 
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FORMATEUR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String refFormateur =ServiceDivers.generateRef("FM");
    @Column(nullable = false, unique = true)
    private String idUniq;
    @Column(nullable = false, unique = true)
    private String cin; 
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Africa/Tunis" )
    @Column(nullable = false)
    private LocalDate dateNaissance; 
    @Column(unique = true)
    private String email;
    private String telFixe;
    private String tel1;
    private String tel2;  
    private String adresse;
   // private String diplome; // peut etre supprimé si on utilise la liste des diplomes
    private String grade ;
    private String degre ; // pour cycle superieur
    private String fonctionProfessionnelle ;
    private String ministere ;
    private String direction ;
    private String banque ;
    @Column(name = "rib", length = 20)
    private String rib ;
    
    @OneToMany( mappedBy = "formateur", cascade = CascadeType.ALL, orphanRemoval = true ) 
    @JsonIgnoreProperties({"formateur", "fichier"}) 
    private List<DocumentFormateur> documentsFormateur = new ArrayList<>();

    @OneToMany( mappedBy = "formateur", cascade = CascadeType.ALL, orphanRemoval = true ) 
    @JsonIgnoreProperties({"formateur" }) 
    private List<CandidatureSpontanee> candidatureSpontanees = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieFormateur categorieFormateur; 

    public String getCategorieFormateurLabel() {
        return categorieFormateur != null
                ? categorieFormateur.getLabel()
                : null;
    }



}