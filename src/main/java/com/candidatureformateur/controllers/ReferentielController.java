package com.candidatureformateur.controllers;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController;
 
import com.candidatureformateur.entity.ThemeFormation;
import com.candidatureformateur.entity.TypeDocument;
import com.candidatureformateur.exceptions.ApiException;
import com.candidatureformateur.repository.CandidaturePlanifieeRepository;
import com.candidatureformateur.repository.CandidatureSpontaneeRepository;
import com.candidatureformateur.repository.DocumentDemandeRepository;
import com.candidatureformateur.repository.DocumentFormateurRepository;
import com.candidatureformateur.repository.ThemeFormationRepository;
import com.candidatureformateur.repository.TypeDocumentRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/apicandidatureFormateurs/referentiel")
@AllArgsConstructor 

public class ReferentielController  {
         
  private final ThemeFormationRepository themeFormationRepository; 
  private final TypeDocumentRepository typeDocumentRepository;
  private final CandidaturePlanifieeRepository candidaturePlanifieeRepository;
  private final CandidatureSpontaneeRepository candidatureSpontaneeRepository;
  private final DocumentFormateurRepository documentFormateurRepository;
  private final DocumentDemandeRepository documentDemandeRepository;
  
   @GetMapping("/getAllTypeDocuments")
public Map<String, Object> getAllTypeDocuments() {

     List<TypeDocument> typeDocuments =
            typeDocumentRepository.findAll(); 
    return Map.of(
            "action", "SUCCESS",
            "typeDocuments", typeDocuments
    );
} 
  @GetMapping("/getAllThemesByFormateur/{idFormateur}")
public Map<String, Object> getAllThemesByFormateur(@PathVariable Long idFormateur) {

     List<ThemeFormation> themeFormations =
            themeFormationRepository.findThemesNonInscritsByFormateur(idFormateur);//findAll(); 
    return Map.of(
            "action", "SUCCESS",
            "themeFormations", themeFormations
    );
}
  
   @GetMapping("/findThemes")
public Map<String, Object> findThemes( ) {

     List<ThemeFormation> themes =
            themeFormationRepository.findAll();//findAll(); 
    return Map.of(
            "action", "SUCCESS",
            "data", themes
    );
}
@PostMapping("/ajouterTheme")
public Map<String, Object> ajouterTheme(
        @RequestBody ThemeFormation themeFormation) {

    themeFormation.setId(null); 
    themeFormation =  themeFormationRepository.save(themeFormation); 
    List<ThemeFormation> allThemes =  themeFormationRepository.findAll();  
    Map<String, Object> response = new HashMap<>();
    response.put("action", "SUCCESS");
    response.put("message", "تمت إضافة محور التكوين بنجاح");
    response.put("data", allThemes);
    return response;
}
@PutMapping("/modifierTheme")
public Map<String, Object> modifierTheme( @RequestBody ThemeFormation themeFormation) {
    ThemeFormation themeExistant =
            themeFormationRepository.findById(themeFormation.getId())
                    .orElseThrow(() -> new ApiException(
                            "ERROR",
                            "محور التكوين غير موجود"
                    ));

    themeExistant.setLibelle(themeFormation.getLibelle());
    themeExistant.setDescription(themeFormation.getDescription());
    themeExistant =  themeFormationRepository.save(themeExistant);
    List<ThemeFormation> allThemes = themeFormationRepository.findAll();
    Map<String, Object> response = new HashMap<>();
    response.put("action", "SUCCESS");
    response.put("message", "تم تعديل محور التكوين بنجاح");
    response.put("data", allThemes);

    return response;
}

@GetMapping("/getAllTypesDocuments")
public Map<String, Object> getAllTypesDocuments() {

    List<TypeDocument> allTypesDocuments =
            typeDocumentRepository.findAll();

    Map<String, Object> response = new HashMap<>();

    response.put("action", "SUCCESS");
    response.put(
            "message",
            "تم جلب قائمة أنواع الوثائق بنجاح"
    );
    response.put("data", allTypesDocuments);

    return response;
}
@PostMapping("/ajouterTypeDocument")
public Map<String, Object> ajouterTypeDocument(
        @RequestBody TypeDocument typeDocument) {

    typeDocument.setId(null);

    typeDocument =
            typeDocumentRepository.save(typeDocument);

    List<TypeDocument> allTypesDocuments =
            typeDocumentRepository.findAll();

    Map<String, Object> response = new HashMap<>();

    response.put("action", "SUCCESS");
    response.put(
            "message",
            "تمت إضافة نوع الوثيقة بنجاح"
    );
    response.put("data", allTypesDocuments);

    return response;
}
@PutMapping("/modifierTypeDocument")
public Map<String, Object> modifierTypeDocument(
        @RequestBody TypeDocument typeDocument) {

    TypeDocument typeDocumentExistant =
            typeDocumentRepository.findById(typeDocument.getId())
                    .orElseThrow(() -> new ApiException(
                            "ERROR",
                            "نوع الوثيقة غير موجود"
                    ));

    typeDocumentExistant.setCodeDocument(
            typeDocument.getCodeDocument()
    );

    typeDocumentExistant.setLibelle(
            typeDocument.getLibelle()
    );

    typeDocumentExistant.setNbDocAutorise(
            typeDocument.getNbDocAutorise()
    );

    typeDocumentExistant =
            typeDocumentRepository.save(typeDocumentExistant);

    List<TypeDocument> allTypesDocuments =
            typeDocumentRepository.findAll();

    Map<String, Object> response = new HashMap<>();

    response.put("action", "SUCCESS");
    response.put(
            "message",
            "تم تعديل نوع الوثيقة بنجاح"
    );
    response.put("data", allTypesDocuments);

    return response;
}

@DeleteMapping("/supprimerTheme/{idTheme}")
public ResponseEntity<?> supprimerTheme(@PathVariable Long idTheme) {

    try {

        // Vérifier que le thème existe
        ThemeFormation theme = themeFormationRepository.findById(idTheme)
                .orElseThrow(() ->  new ApiException("ERROR","الموضوع المطلوب غير موجود"));
        // Vérifier si le thème est utilisé dans une candidature planifiée
        if (candidaturePlanifieeRepository.existsByThemeFormationId(idTheme)) {
            throw new ApiException("ERROR", "لا يمكن حذف هذا الموضوع لأنه مرتبط بترشح مفتوح" );
        }
        // Vérifier si le thème est utilisé dans une candidature spontanée
        if (candidatureSpontaneeRepository.existsByThemeFormationId(idTheme)) {
            throw new ApiException( "ERROR","لا يمكن حذف هذا الموضوع لأنه مرتبط بترشح تلقائي"  );
        }
        // Suppression
        themeFormationRepository.delete(theme);
        // Retourner la liste actualisée
        List<ThemeFormation> allThemes = themeFormationRepository.findAll();
        return ResponseEntity.ok(
                Map.of(
                        "data", allThemes,
                        "message", "تم حذف الموضوع بنجاح"
                )
        );
    } catch (ApiException e) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message", e.getMessage()
                ));

    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "حدث خطأ أثناء حذف الموضوع"
                ));
    }
}

@DeleteMapping("/supprimeridTypeDocument/{idTypeDocument}")
public ResponseEntity<?> supprimerTypeDocument(@PathVariable Long idTypeDocument) {

    try {

        // Vérifier que le type de document existe
        TypeDocument typeDocument = typeDocumentRepository.findById(idTypeDocument)
                .orElseThrow(() ->  new ApiException("ERROR","نوع الوثيقة المطلوب غير موجود"));

        // Vérifier si le type de document est utilisé par DocumentDemande
        if (documentDemandeRepository.existsByTypeDocumentId(idTypeDocument)) {
            throw new ApiException("ERROR", "لا يمكن حذف نوع الوثيقة لأنه مرتبط بوثيقة مطلوبة" );
        }
        // Vérifier si le type de document est utilisé par DocumentFormateur
        if (documentFormateurRepository.existsByTypeDocumentId(idTypeDocument)) {
            throw new ApiException("ERROR", "لا يمكن حذف نوع الوثيقة لأنه مرتبط بوثيقة تكوين"  );
        }
        // Suppression
        typeDocumentRepository.delete(typeDocument);

        // Retourner la liste actualisée
        List<TypeDocument> allTypesDocuments =  typeDocumentRepository.findAll();

        return ResponseEntity.ok(
                Map.of(
                        "data", allTypesDocuments,
                        "message", "تم حذف نوع الوثيقة بنجاح"
                )
        );

    } catch (ApiException e) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message", e.getMessage()
                ));

    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "حدث خطأ أثناء حذف نوع الوثيقة"
                ));
    }
}
}