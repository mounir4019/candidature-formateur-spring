package com.candidatureformateur.controllers;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
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
import com.candidatureformateur.repository.ThemeFormationRepository;
import com.candidatureformateur.repository.TypeDocumentRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/apicandidatureFormateurs/referentiel")
@AllArgsConstructor 

public class ReferentielController  {
         
  private final ThemeFormationRepository themeFormationRepository; 
  private final TypeDocumentRepository typeDocumentRepository;
  
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
}