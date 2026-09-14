package com.candidatureformateur.entity;
import java.time.LocalDateTime;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DOCUMENT_FORMATEUR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
 
@JsonIgnoreProperties({"fichier","formateur", "hibernateLazyInitializer", "handler"})
public class DocumentFormateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) 
    private String refDocumentFormateur =ServiceDivers.generateRef("DF");

    @Column( )
    private String nomFichier;

    @Lob
    @JsonIgnore
    @Column(columnDefinition = "LONGBLOB", nullable = false)
   // @JsonIgnore
    private byte[] fichier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formateur_id", nullable = false)
   // @JsonIgnoreProperties({"documentsFormateur", "hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Formateur formateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_document_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TypeDocument typeDocument;
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd",
        timezone = "Africa/Tunis"
    )
    @Column(nullable = false)
    private LocalDateTime dateCreation=LocalDateTime.now(ZoneId.of("Africa/Tunis"));

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd",
        timezone = "Africa/Tunis"
    )
    @Column(nullable = true)
    private LocalDateTime dateDerniereModification;
}