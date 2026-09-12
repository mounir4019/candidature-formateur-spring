package com.candidatureformateur.entity;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.candidatureformateur.servicesDivers.ServiceDivers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CANDIDATURE_PLANIFIEE")
@Getter
@Setter
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class CandidaturePlanifiee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String refCandidaturePlanifiee=ServiceDivers.generateRef("CP");
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Africa/Tunis" )
    @Column(nullable = false)
    private LocalDate dd;

    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Africa/Tunis" )
    @Column(nullable = false)
    private LocalDate df; 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_formation_id", nullable = false) 
    private ThemeFormation themeFormation;

    @OneToMany(  mappedBy = "candidaturePlanifiee",  cascade = CascadeType.ALL,  orphanRemoval = true )
    @JsonIgnoreProperties("candidaturePlanifiee")
    private List<DocumentReglement> documentsReglement = new ArrayList<>(); 

   
    
    @OneToMany( mappedBy = "candidaturePlanifiee", cascade = CascadeType.ALL, orphanRemoval = true ) 
    @JsonIgnoreProperties("candidaturePlanifiee") 
    private List<DocumentDemande> documentsDemandes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "structure_id", nullable = false)  
    private Structure structure;
    @Column(name = "admin_creation", nullable = false)
    private String adminCreation;
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Africa/Tunis" )
    @Column(nullable = false)
    private LocalDate dateCreation=LocalDate.now(ZoneId.of("Africa/Tunis")); 
    
    private boolean approuvee =false;

    public String getEtat() {

    LocalDate now = LocalDate.now(
            ZoneId.of("Africa/Tunis")
    );

    if (!approuvee) {

        if (df != null && df.isBefore(now)) {
            return "ANOMALIE";
        }

        return "NON_APPROUVEE";
    }

    if (dd != null && now.isBefore(dd)) {
        return "ATTENTE";
    }

    if (df != null && now.isAfter(df)) {
        return "CLOTUREE";
    }

    return "DEMARREE";
}


public String getEtatLabel() {

    return switch (getEtat()) {

        case "NON_APPROUVEE" ->
                "لم تتم المصادقة";

        case "ANOMALIE" ->
                "خطأ في الجدولة";

        case "ATTENTE" ->
                "في انتظار تاريخ البداية";

        case "DEMARREE" ->
                "في طور استقبال الترشحات";

        case "CLOTUREE" ->
                "تم غلق الترشحات";

        default ->
                "حالة غير معروفة";
    };
}
}