package com.candidatureformateur.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.candidatureformateur.entity.Formateur;
import com.candidatureformateur.exceptions.ApiException;
import com.candidatureformateur.repository.FormateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormateurService {

    private final FormateurRepository formateurRepository;

public Map<String, Object> rechercherParCinIdUniqEtDateNaissance(
        String cin,
        String idUniq,
        String dateNaissanceStr) {

    if (cin == null || cin.trim().isEmpty()
            || idUniq == null || idUniq.trim().isEmpty()
            || dateNaissanceStr == null || dateNaissanceStr.trim().isEmpty()) {

        throw new ApiException( 
                 "ERROR",
               "يرجى إدخال رقم بطاقة التعريف والمعرف الفريد وتاريخ الولادة."
         );
    }

    cin = cin.trim();
    idUniq = idUniq.trim();

    LocalDate dateNaissance;

    try {
        dateNaissance = LocalDate.parse(dateNaissanceStr.trim());
    } catch (DateTimeParseException e) {

        throw new ApiException( 
                 "ERROR",
             "صيغة تاريخ الولادة غير صحيحة."
        );
    }

    // 1) Recherche avec les 3 paramètres
    Optional<Formateur> formateur =
            formateurRepository.findByCinAndIdUniqAndDateNaissance(
                    cin,
                    idUniq,
                    dateNaissance
            );

    // Formateur existant avec les 3 paramètres
    if (formateur.isPresent()) {

        return Map.of(
                "action", "SUCCESS",
                "foundByAll", true,
                "foundByOneIdentifier", true,
                "formateur", formateur.get(),
                "message", "تم العثور على بيانات المكوّن."
        );
    }

    // 2) Aucun formateur avec les 3 paramètres
    //    On vérifie si le CIN ou l'idUniq existe déjà

    boolean cinExiste =
            formateurRepository.existsByCin(cin);

    boolean idUniqExiste =
            formateurRepository.existsByIdUniq(idUniq);

    // CIN ou ID unique déjà utilisé
    if (cinExiste || idUniqExiste) {

        return Map.of(
                "action", "SUCCESS",
                "foundByAll", false,
                "foundByOneIdentifier", true,
                "message",  "المعطات غير متطابقة، الرجاء التثبت"
        );
    }

    // 3) Aucun CIN et aucun idUniq trouvé
    //    => nouveau formateur
    return Map.of(
            "action", "SUCCESS",
            "foundByAll", false,
            "foundByOneIdentifier", false,
            "message", "يمكن تسجيل مكوّن جديد."
    );
}
public Map<String, Object>  ajouterFormateur(Formateur formateur) {
    // Vérification si le CIN ou l'idUniq existe déjà
    boolean cinExiste = formateurRepository.existsByCin(formateur.getCin());
    boolean idUniqExiste = formateurRepository.existsByIdUniq(formateur.getIdUniq());

    if (cinExiste || idUniqExiste) {
        throw new ApiException( 
               "ERROR",
                "المعطات غير متطابقة، الرجاء التثبت"
       );
    }

    // Enregistrement du nouveau formateur
    Formateur formateurEnregistre = formateurRepository.save(formateur);

    return Map.of(
            "action", "SUCCESS",
            "formateur", formateurEnregistre,
            "message", "تم تسجيل المكوّن بنجاح."
    );
}
@Transactional
public Map<String, Object> modifierFormateur(Formateur formateur) {

    // Vérifier que l'identifiant existe
    if (formateur.getId() == null) {
        throw new ApiException( 
                 "ERROR",
                "معرّف المكوّن إجباري لتعديل البيانات."
         );
    }

    // Rechercher le formateur existant
    Formateur formateurExistant = formateurRepository
            .findById(formateur.getId())
            .orElseThrow(() -> new ApiException( 
                   "ERROR",
                   "المكوّن غير موجود."
          ));
        // verifier si un autre formateur enregistré par le meme cin ou idUniq.    
        boolean cinExiste = formateurRepository.existsByCinAndIdNot( formateur.getCin(), formateur.getId()); 
        boolean idUniqExiste = formateurRepository.existsByIdUniqAndIdNot( formateur.getIdUniq(), formateur.getId() ); 
        if (cinExiste || idUniqExiste) {
        throw new ApiException( 
                 "ERROR",
                 "يوجد مكوّن آخر مسجّل بنفس المعطيات."
        );
        }
// Informations personnelles
    formateurExistant.setNom(formateur.getNom());
    formateurExistant.setPrenom(formateur.getPrenom());
    formateurExistant.setEmail(formateur.getEmail());
    formateurExistant.setTelFixe(formateur.getTelFixe());
    formateurExistant.setTel1(formateur.getTel1());
    formateurExistant.setTel2(formateur.getTel2());
    formateurExistant.setAdresse(formateur.getAdresse());

    // Informations professionnelles
    /* * Informations d'identification * CIN, idUniq et dateNaissance * ne sont pas modifiés. */
    formateurExistant.setGrade(formateur.getGrade());
    formateurExistant.setDegre(formateur.getDegre());
    formateurExistant.setFonctionProfessionnelle(
            formateur.getFonctionProfessionnelle()
    );
    formateurExistant.setMinistere(formateur.getMinistere());
    formateurExistant.setDirection(formateur.getDirection());

    // Informations bancaires
    formateurExistant.setBanque(formateur.getBanque());
    formateurExistant.setRib(formateur.getRib());

    // Catégorie du formateur
    formateurExistant.setCategorieFormateur(
            formateur.getCategorieFormateur()
    );

    Formateur formateurModifie =
            formateurRepository.save(formateurExistant);

    return Map.of(
            "action", "SUCCESS",
            "formateur", formateurModifie,
            "message", "تم تعديل بيانات المكوّن بنجاح."
    );
}
}