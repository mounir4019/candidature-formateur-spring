package com.candidatureformateur.controllers;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.candidatureformateur.entity.CandidaturePlanifiee;
import com.candidatureformateur.entity.CandidatureSpontanee;
import com.candidatureformateur.entity.DocumentDemande;
import com.candidatureformateur.entity.DocumentFormateur;
import com.candidatureformateur.entity.DocumentParticipationPlanifiee;
import com.candidatureformateur.entity.DocumentReglement;
import com.candidatureformateur.entity.Formateur;
import com.candidatureformateur.entity.ParticipationPlanifiee;
import com.candidatureformateur.entity.Structure;
import com.candidatureformateur.entity.ThemeFormation;
import com.candidatureformateur.entity.TypeDocument;
import com.candidatureformateur.entity.dto.CandidaturePlanifieeRequest;
import com.candidatureformateur.entity.dto.DocumentParticipationPlanifieeRequest;
import com.candidatureformateur.entity.dto.ParticipationPlanifieeRequest;
import com.candidatureformateur.exceptions.ApiException;
import com.candidatureformateur.repository.CandidaturePlanifieeRepository;
import com.candidatureformateur.repository.CandidatureSpontaneeRepository;
import com.candidatureformateur.repository.DocumentDemandeRepository;
import com.candidatureformateur.repository.DocumentFormateurRepository;
import com.candidatureformateur.repository.DocumentParticipationPlanifieeRepository;
import com.candidatureformateur.repository.DocumentReglementRepository;
import com.candidatureformateur.repository.FormateurRepository;
import com.candidatureformateur.repository.ParticipationPlanifieeRepository;
import com.candidatureformateur.repository.StructureRepository;
import com.candidatureformateur.repository.ThemeFormationRepository;
import com.candidatureformateur.repository.TypeDocumentRepository;
import com.candidatureformateur.servicesDivers.ConvertPdfService;
import com.candidatureformateur.servicesDivers.ServiceDivers;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
@RestController
@RequestMapping("/api/candidatureFormateurs/candidaturePlanifiee")
@AllArgsConstructor  
public class CandidaturePlanifieeController {
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList("image/jpeg","image/jpg", "image/png", "image/gif"); 
    private final CandidaturePlanifieeRepository candidaturePlanifieeRepository; 
    private final FormateurRepository formateurRepository;
    private final ParticipationPlanifieeRepository participationPlanifieeRepository;
    private final DocumentDemandeRepository documentDemandeRepository;
    private final DocumentFormateurRepository documentFormateurRepository;
    private final DocumentParticipationPlanifieeRepository   documentParticipationPlanifieeRepository;
    private final StructureRepository   structureRepository;
    private final ThemeFormationRepository   themeFormationRepository;
    private final DocumentReglementRepository documentReglementRepository; 
    private final TypeDocumentRepository typeDocumentRepository; 
     
    @GetMapping("/getListeCandidaturePlanifieesPublique")
    public List<CandidaturePlanifiee> getListeCandidaturePlanifiees() {

        LocalDate now = LocalDate.now(ZoneId.of("Africa/Tunis"));

        return candidaturePlanifieeRepository
                .findByApprouveeTrueAndDdLessThanEqualAndDfGreaterThanEqual(now, now);
                
    }
    @GetMapping("/getCandidaturePlanifiee/{refCP}")
public CandidaturePlanifiee getCandidaturePlanifiee(
        @PathVariable String refCP) {

    return candidaturePlanifieeRepository.findByRefCandidaturePlanifiee(refCP)
            .orElseThrow(() -> new ApiException( 
                      "ERROR",
                    "الترشح المفتوح غير موجود."
             ));
  }
 
@GetMapping("/get10DernieresParticipationsPlanifiees/{idFormateur}")
public List<Map<String, Object>> get10DernieresParticipationsPlanifiees(
        @PathVariable Long idFormateur) {

    /*
     * 1. Vérifier que le formateur existe
     */
    formateurRepository
            .findById(idFormateur)
            .orElseThrow(() ->
                    new ApiException(
                            "error",
                            "لا يتم العثور على هذا المكون"
                    )
            );

    /*
     * 2. Récupérer les 10 dernières participations
     */
    List<ParticipationPlanifiee> participations =
            participationPlanifieeRepository
                    .findTop10ByFormateurIdOrderByDateSoumissionDesc(idFormateur);

    /*
     * 3. Préparer la réponse
     */
    List<Map<String, Object>> response = new ArrayList<>();

    for (ParticipationPlanifiee participation : participations) {

        Map<String, Object> item = new HashMap<>();

        item.put("id", participation.getId());
        item.put("dateSoumission", participation.getDateSoumission());

        /*
         * Informations du formateur si nécessaire
         */
        item.put("formateur", participation.getFormateur());

        /*
         * Informations de la candidature planifiée
         */
        CandidaturePlanifiee candidaturePlanifiee =
                participation.getCandidaturePlanifiee();

        if (candidaturePlanifiee != null) {

            Map<String, Object> candidature = new HashMap<>();

            candidature.put(
                    "themeFormation",
                    candidaturePlanifiee.getThemeFormation()
            );

            candidature.put(
                    "structure",
                    candidaturePlanifiee.getStructure()
            );

            item.put("candidaturePlanifiee", candidature);
        }

        response.add(item);
    }

    return response;
}

 
@GetMapping("/getListeCandidaturePlanifieesByStructure/{codeStructure}")
public ResponseEntity<?> getListeCandidaturePlanifieesByStructure(
        @PathVariable String codeStructure,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    LocalDate now = LocalDate.now(
            ZoneId.of("Africa/Tunis")
    );

    LocalDate dateLimite = now.minusYears(5);

    Pageable pageable = PageRequest.of(
            page,
            size
    );
   Structure structure = structureRepository.findByCodeStructure(codeStructure) 
                        .orElseThrow(() ->
                                        new ApiException("error",
                                                "هيكل غير موجود"
                                        )
                                );
    
    Page<CandidaturePlanifiee> result =
            candidaturePlanifieeRepository
                    .findByStructureAndDateCreationGreaterThanEqualOrderByDateCreationDesc(
                            structure,
                            dateLimite,
                            pageable
                    );

    return ResponseEntity.ok(
            Map.of(
                    "content", result.getContent(),
                    "page", result.getNumber(),
                    "size", result.getSize(),
                    "totalElements", result.getTotalElements(),
                    "totalPages", result.getTotalPages(),
                    "hasNext", result.hasNext()
            )
    );
}
@PostMapping("/ajouterParticipationPlanifiee")
@Transactional
public Map<String, Object> ajouterParticipationPlanifiee(
        /** ParticipationPlanifieeRequest-->
         * (idCandidaturePlanifiee, idFormateur, List<DocumentParticipationPlanifieeRequest> documents)
         * DocumentParticipationPlanifieeRequest-->(idDocumentDemande, idDocumentFormateur)
         *  */
        @RequestBody ParticipationPlanifieeRequest request) {

    /*
     * ============================================================
     * 1. Vérification de la candidature planifiée
     * ============================================================
     */

    CandidaturePlanifiee candidaturePlanifiee =
            candidaturePlanifieeRepository
                    .findById(request.getIdCandidaturePlanifiee())
                    .orElseThrow(() ->
                            new ApiException("error",
                                    "لا يتم العثور هذا ترشح مفتوح "
                            )
                    );


    /*
     * ============================================================
     * 2. Vérification de la période de candidature
     * ============================================================
     */

    LocalDate now = LocalDate.now(
            ZoneId.of("Africa/Tunis")
    );

    if (now.isBefore(candidaturePlanifiee.getDd())
            || now.isAfter(candidaturePlanifiee.getDf())) {

        throw new ApiException("error",
                "الترشح المفتوح مغلق حاليا"
        );
    }


    /*
     * ============================================================
     * 3. Vérification du formateur
     * ============================================================
     */

    Formateur formateur =
            formateurRepository
                    .findById(request.getIdFormateur())
                    .orElseThrow(() ->
                            new ApiException("error",
                                    "المكون غير موجود"
                            )
                    );


    /*
     * ============================================================
     * 4. Vérifier si le formateur a déjà participé
     * ============================================================
     */

    boolean dejaParticipe =
            participationPlanifieeRepository
                    .existsByFormateurIdAndCandidaturePlanifieeId(
                            formateur.getId(),
                            candidaturePlanifiee.getId()
                    );
  System.out.println("dejaParticipe::"+formateur.getId()+candidaturePlanifiee.getId()+dejaParticipe);
    if (dejaParticipe) {

        throw new ApiException("error",
                "لقد شارك المكون الحالي  بالترشح المفتوح"
        );
    }


    /*
     * ============================================================
     * 5. Documents reçus dans la requête
     * ============================================================
     * DocumentParticipationPlanifieeRequest-->(idDocumentDemande, idDocumentFormateur);
     */
     
    List<DocumentParticipationPlanifieeRequest> documentsRequest =
            request.getDocuments() != null
                    ? request.getDocuments()
                    : new ArrayList<>();


    /*
     * ============================================================
     * 6. Vérification des documents obligatoires
     * ============================================================
     */

    for (DocumentDemande documentDemande :
            candidaturePlanifiee.getDocumentsDemandes()) {

        if (documentDemande.isObligatoire()) {

            boolean documentFourni =
                    documentsRequest.stream()
                            .anyMatch(d ->
                                    d.getIdDocumentDemande() != null
                                    && d.getIdDocumentFormateur() != null
                                    && d.getIdDocumentDemande()
                                            .equals(documentDemande.getId())
                            );

            if (!documentFourni) {

                throw new ApiException("error",
                        "الوثيقة الإجبارية \""
                                + documentDemande
                                    .getTypeDocument()
                                    .getLibelle()
                                + "\" لم يقع توفيرها."
                );
            }
        }
    }


    /*
     * ============================================================
     * 7. Création de la ParticipationPlanifiee
     * ============================================================
     */

    ParticipationPlanifiee participationPlanifiee =
            new ParticipationPlanifiee();

    participationPlanifiee.setDateSoumission(now);

    participationPlanifiee.setFormateur(
            formateur
    );

    participationPlanifiee.setCandidaturePlanifiee(
            candidaturePlanifiee
    );


    /*
     * ============================================================
     * 8. Sauvegarde de la ParticipationPlanifiee
     * ============================================================
     */

    ParticipationPlanifiee participationSauvegardee =
            participationPlanifieeRepository
                    .save(participationPlanifiee);


    /*
     * ============================================================
     * 9. Création des DocumentParticipationPlanifiee
     * ============================================================
     */

    List<DocumentParticipationPlanifiee>
            documentsParticipation = new ArrayList<>();


    for (DocumentParticipationPlanifieeRequest documentRequest :
            documentsRequest) {

        /*
         * --------------------------------------------------------
         * 9.1 Si aucun document n'est sélectionné
         * --------------------------------------------------------
         *
         * Cela peut arriver pour un document facultatif.
         */

        if (documentRequest.getIdDocumentFormateur() == null) {
            continue;
        }


        /*
         * --------------------------------------------------------
         * 9.2 Vérification du DocumentDemande
         * --------------------------------------------------------
         */

        DocumentDemande documentDemande =
                documentDemandeRepository
                        .findById(
                                documentRequest.getIdDocumentDemande()
                        )
                        .orElseThrow(() ->
                                new ApiException("error",
                                        "الوثيقة المطلوبة غير متوفرة."
                                )
                        );


        /*
         * --------------------------------------------------------
         * 9.3 Vérifier que le DocumentDemande appartient
         *     à la candidature planifiée
         * --------------------------------------------------------
         */

        if (!documentDemande
                .getCandidaturePlanifiee()
                .getId()
                .equals(candidaturePlanifiee.getId())) {

            throw new ApiException("error",
                    //"Le document demandé ne correspond pas à cette candidature."
                    "الوثيقة المطلوبة ليست ضمن وثائق الترشح المفتوح"
            );
        }


        /*
         * --------------------------------------------------------
         * 9.4 Vérification du DocumentFormateur
         * --------------------------------------------------------
         */

        DocumentFormateur documentFormateur =
                documentFormateurRepository
                        .findById(
                                documentRequest
                                        .getIdDocumentFormateur()
                        )
                        .orElseThrow(() ->
                                new ApiException("error",
                                        "وثيقة المكون غير موجودة."
                                )
                        );


        /*
         * --------------------------------------------------------
         * 9.5 Vérifier que le document appartient au formateur
         * --------------------------------------------------------
         */

        if (!documentFormateur
                .getFormateur()
                .getId()
                .equals(formateur.getId())) {

            throw new ApiException("error",
                    "الوثيقة المختارة لا تنتمي للمكون."
            );
        }


        /*
         * --------------------------------------------------------
         * 9.6 Vérifier le type du document
         * --------------------------------------------------------
         */

        if (!documentFormateur
                .getTypeDocument()
                .getId()
                .equals(
                        documentDemande
                                .getTypeDocument()
                                .getId()
                )) {

            throw new ApiException("error",
                    "نوعية الوثيقة المختارة  ليست ضمن نوعية الوثائق المطلوبة"
            );
        }


        /*
         * --------------------------------------------------------
         * 9.7 Création du DocumentParticipationPlanifiee
         * --------------------------------------------------------
         */

        DocumentParticipationPlanifiee
                documentParticipation =
                        new DocumentParticipationPlanifiee();

        documentParticipation.setParticipationPlanifiee(
                participationSauvegardee
        );

        documentParticipation.setDocumentDemande(
                documentDemande
        );

        documentParticipation.setDocumentFormateur(
                documentFormateur
        );


        /*
         * --------------------------------------------------------
         * 9.8 Sauvegarde du document
         * --------------------------------------------------------
         */

        DocumentParticipationPlanifiee
                documentParticipationSauvegarde =
                        documentParticipationPlanifieeRepository
                                .save(documentParticipation);


        documentsParticipation.add(
                documentParticipationSauvegarde
        );
    }


    /*
     * ============================================================
     * 10. Réponse
     * ============================================================
     *
     * ParticipationPlanifiee ne possède PAS de liste documents.
     *
     * On retourne donc séparément :
     * - participationPlanifiee
     * - documentsParticipation
     */

    Map<String, Object> response = new HashMap<>();

    response.put(
            "action",
            "SUCCESS"
    );

    response.put(
            "message",
            "تم تقديم الترشح بنجاح."
    );

    response.put(
            "participationPlanifiee",
            participationSauvegardee
    );

    response.put( "documentsParticipation", documentsParticipation );

    return response;
}
@PostMapping("/ajouterCandidaturePlanifiee")
public Map<String, Object> ajouterCandidaturePlanifiee(
        @RequestBody CandidaturePlanifieeRequest request,
        @AuthenticationPrincipal Jwt jwt) {

    try {

        String username = jwt.getClaimAsString("preferred_username");
        String codeStructure = jwt.getClaimAsString("structure");
        System.out.println("JWT ::: " + jwt.getClaims());
        Structure structure =structureRepository
        .findByCodeStructure(codeStructure)
        .orElseThrow(
                () -> new ApiException( "ERROR", "خطأ بمعرفة هيكل الأدمين")
        );

         /*  .orElseThrow(
                () -> new ApiException( 
                      "ERROR",
                    "الترشح المفتوح غير موجود."
             )); */
        ThemeFormation themeFormation = themeFormationRepository
            .findById(request.getIdTheme())
            .orElseThrow(() -> new ApiException(
                    "ERROR",
                    "محور التكوين غير موجود"
            ));

        CandidaturePlanifiee candidaturePlanifiee = new CandidaturePlanifiee(); 
        candidaturePlanifiee.setThemeFormation(themeFormation);
        candidaturePlanifiee.setAdminCreation(username);
        candidaturePlanifiee.setStructure(structure);
        candidaturePlanifiee.setDd(request.getDd());
        candidaturePlanifiee.setDf(request.getDf());
        CandidaturePlanifiee result =  candidaturePlanifieeRepository.save(candidaturePlanifiee);

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "تم تسجيل الترشح بنجاح");
        response.put("data", result);

        return response;

    } catch (ApiException e) {
        throw e;

    } catch (Exception e) { 
        e.printStackTrace(); 
        throw new ApiException("ERROR",
                "حدث خطأ أثناء تسجيل الترشح"
        );
    }
}
@PutMapping("/modifierCandidaturePlanifee/{idCandidaturePlanifiee}")
@Transactional
public Map<String, Object> modifierCandidaturePlanifee(
        @PathVariable Long idCandidaturePlanifiee,
        @RequestBody CandidaturePlanifieeRequest request) {

    try {

        CandidaturePlanifiee candidaturePlanifiee =
                candidaturePlanifieeRepository
                        .findById(idCandidaturePlanifiee)
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "الترشح المفتوح غير موجود"
                        ));
        if(candidaturePlanifiee.isApprouvee()){
                throw new   ApiException(
                                "ERROR",
                                "تمت المصادقة على الترشح المفتوح: لا يمكن التحيين "
                        );
        }
        candidaturePlanifiee.setDd(request.getDd());
        candidaturePlanifiee.setDf(request.getDf());

        ThemeFormation themeFormation =
                themeFormationRepository
                        .findById(request.getIdTheme())
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "محور التكوين غير موجود"
                        ));

        candidaturePlanifiee.setThemeFormation(themeFormation);

        CandidaturePlanifiee result =
                candidaturePlanifieeRepository.save(
                        candidaturePlanifiee
                );

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "تم تعديل الترشح بنجاح");
        response.put("data", result);

        return response;

    } catch (ApiException e) {
        throw e;

    } catch (Exception e) {
        e.printStackTrace();

        throw new ApiException(
                "ERROR",
                "حدث خطأ أثناء تعديل الترشح"
        );
    }
}
@GetMapping("/getDocumentFormateur/{idDocument}")
public Map<String, Object> getDocumentFormateur(
        @PathVariable Long idDocument) {

    DocumentFormateur documentFormateur =
            documentFormateurRepository.findById(idDocument)
                    .orElseThrow(() -> new ApiException( 
                            "ERROR",
                           "الوثيقة غير موجودة."
                   ));

    return Map.of(
            "action", "SUCCESS",
            "message", "تم العثور على الوثيقة.",
            "document", documentFormateur
    );
}

@PostMapping("ajouterDocumentReglement/{idCandidaturePlanifiee}")
    public Map<String, Object> ajouterDocumentReglement( 
            @RequestParam(value = "file", required = false) MultipartFile  file, 
            @PathVariable Long idCandidaturePlanifiee
             )  throws ParseException, IOException{
                 
              CandidaturePlanifiee candidaturePlanifiee   = candidaturePlanifieeRepository
            .findById(idCandidaturePlanifiee)
            .orElseThrow(() -> new ApiException( 
                    "ERROR",
                   "الترشح المفتوح غير موجود."
             ));
 
           DocumentReglement documentReglement= new DocumentReglement() ;  
           documentReglement.setLibelle(file.getOriginalFilename()); 
          // documentFormateur.setsetTypeFichier(typeFichier);
            
           if (file != null && !file.isEmpty()) {
            //documentCandidat.setFile(file.getBytes());
            //byte[] fileContent;
            String mimeType = file.getContentType();

            if (mimeType != null && IMAGE_MIME_TYPES.contains(mimeType)) {
                // Si c'est une image, la convertir en PDF
                documentReglement.setFichier( ConvertPdfService.convertImageToPDF(file));
            } else {
                // Si c'est un PDF, récupérer directement le contenu
                documentReglement.setFichier(file.getBytes());
            }
           }  
        //   Formateur  formateur=formateurRepository.findById(idFormateur).orElse(null); 
            documentReglement.setCandidaturePlanifiee( candidaturePlanifiee); 
            DocumentReglement saved =
            documentReglementRepository.save(documentReglement);
         return Map.of(
            "action", "SUCCESS",
            "message","تم اضافة وثيقة بنجاح",
            "documentsReglement", saved.getCandidaturePlanifiee().getDocumentsReglement()
          );
    }
  @PutMapping("modifierDocumentReglement/{idDocument}")
public Map<String, Object> modifierDocumentReglement(
        @RequestParam(value = "file", required = false) MultipartFile file, 
        @PathVariable Long idDocument
) throws IOException { 
    DocumentReglement documentReglement = documentReglementRepository
            .findById(idDocument)
            .orElseThrow(() -> new ApiException( 
                    "ERROR",
                    "الوثيقة غير موجودة."
            ));  
    // Mise à jour du fichier uniquement si un nouveau fichier est fourni
    if (file != null && !file.isEmpty()) { 
        documentReglement.setLibelle(file.getOriginalFilename()); 
        String mimeType = file.getContentType(); 
        if (mimeType != null && IMAGE_MIME_TYPES.contains(mimeType)) { 
            documentReglement.setFichier(  ConvertPdfService.convertImageToPDF(file)  ); 
        } else { 
            documentReglement.setFichier(file.getBytes());
        }
    } 
    DocumentReglement saved = documentReglementRepository.save(documentReglement);

        // IMPORTANT :
    // on recharge explicitement le Formateur
    
    return Map.of(
            "action", "SUCCESS",
            "message", "تم تحيين الوثيقة بنجاح",
            "documentsReglement", saved.getCandidaturePlanifiee().getDocumentsReglement()
    );
}   
   @GetMapping("/documentReglement/loadPdfFile/{idDocument}")
    public ResponseEntity<byte[]> loadPdfFileDocumentReglement(@PathVariable Long idDocument) {
        // Exemple de récupération de la session, remplacez-le par votre logique réelle
        DocumentReglement documentReglement= documentReglementRepository.findById(idDocument).orElse(null);
        if (documentReglement == null || documentReglement.getFichier() == null) {
            return ResponseEntity.notFound().build();
        } 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("document.pdf")
                .build());

        return new ResponseEntity<>(documentReglement.getFichier(), headers, HttpStatus.OK);
    }

@DeleteMapping("/documentReglement/supprimerDocumentReglement/{idDocument}")
@Transactional
public Map<String, Object> supprimerDocumentReglement( @PathVariable Long idDocument) {
    try {
       
        DocumentReglement documentReglement =
                documentReglementRepository
                        .findById(idDocument)
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "الوثيقة غير موجودة"
                        )); 
         Long idCandidaturePlanifiee=  documentReglement.getCandidaturePlanifiee().getId();      
        documentReglementRepository.delete(documentReglement);
        CandidaturePlanifiee candidaturePlanifiee = candidaturePlanifieeRepository.findById(idCandidaturePlanifiee)
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "الترشح المفتوح غير موجودة"
                        ));
        Map<String, Object> response = new HashMap<>(); 
        response.put("success", true);
        response.put( "message", "تم حذف الوثيقة بنجاح" );
        response.put( "documentsReglement", candidaturePlanifiee.getDocumentsReglement()  );
        return response;
    } catch (ApiException e) {
        throw e;
    } catch (Exception e) {
        e.printStackTrace();
        throw new ApiException(
                "ERROR",
                "حدث خطأ أثناء حذف الوثيقة"
        );
    }
}
@PostMapping("/documentDemande/ajouterDocumentDemande")
@Transactional
public Map<String, Object> ajouterDocumentDemande(
        @RequestBody Map<String, Object> request) {

    try {

        Long idCandidaturePlanifiee =
                Long.valueOf( request.get("idCandidaturePlanifiee").toString()  );

        Long idTypeDocument =
                Long.valueOf( request.get("idTypeDocument").toString() );

        boolean obligatoire =
                Boolean.parseBoolean( request.get("obligatoire").toString() );

        CandidaturePlanifiee candidaturePlanifiee =
                candidaturePlanifieeRepository
                        .findById(idCandidaturePlanifiee)
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "الترشح المفتوح غير موجود"
                        )); 
        TypeDocument typeDocument =
                typeDocumentRepository
                        .findById(idTypeDocument)
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "نوع الوثيقة غير موجود"
                        ));
        boolean existeDeja =
        documentDemandeRepository
                .existsByCandidaturePlanifieeIdAndTypeDocumentId(
                        idCandidaturePlanifiee,
                        idTypeDocument
                );
        if (existeDeja) {
        throw new ApiException(
                "ERROR",
                "هذا النوع من الوثائق مضاف مسبقا لهذا الترشح"
        );
        }
        DocumentDemande documentDemande = new DocumentDemande();
        documentDemande.setCandidaturePlanifiee( candidaturePlanifiee );
        documentDemande.setTypeDocument( typeDocument );
        documentDemande.setObligatoire( obligatoire );
        documentDemandeRepository.save(documentDemande);

        // Relire la candidature pour récupérer la liste actualisée
        CandidaturePlanifiee candidaturePlanifieeUpdated =
                candidaturePlanifieeRepository
                        .findById(idCandidaturePlanifiee)
                        .orElseThrow(() -> new ApiException(
                                "ERROR",
                                "الترشح المفتوح غير موجود"
                        ));
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put( "message", "تمت إضافة الوثيقة بنجاح"  );
        response.put( "data",  candidaturePlanifieeUpdated.getDocumentsDemandes() );
        return response;
    } catch (ApiException e) {
        throw e;
    } catch (Exception e) {
        e.printStackTrace();
        throw new ApiException(
                "ERROR",
                "حدث خطأ أثناء إضافة الوثيقة"
        );
    }
}   
@PutMapping("/documentDemande/modifierDocumentDemande")
@Transactional
public Map<String, Object> modifierDocumentDemande(@RequestBody Map<String, Object> request) {

    try {
        Long idDocumentDemande = Long.valueOf(request.get("idDocumentDemande").toString());
        Long idCandidaturePlanifiee = Long.valueOf(request.get("idCandidaturePlanifiee").toString());
        Long idTypeDocument = Long.valueOf(request.get("idTypeDocument").toString());
        boolean obligatoire = Boolean.parseBoolean(request.get("obligatoire").toString());
        DocumentDemande documentDemande = documentDemandeRepository.findById(idDocumentDemande)
                .orElseThrow(() -> new ApiException("ERROR", "الوثيقة المطلوبة غير موجودة"));
        CandidaturePlanifiee candidaturePlanifiee = candidaturePlanifieeRepository.findById(idCandidaturePlanifiee)
                .orElseThrow(() -> new ApiException("ERROR", "الترشح المفتوح غير موجود"));
        TypeDocument typeDocument = typeDocumentRepository.findById(idTypeDocument)
                .orElseThrow(() -> new ApiException("ERROR", "نوع الوثيقة غير موجود"));

        boolean existeDeja = documentDemandeRepository.existsByCandidaturePlanifieeIdAndTypeDocumentIdAndIdNot(idCandidaturePlanifiee, idTypeDocument, idDocumentDemande);
        if (existeDeja) {
            throw new ApiException("ERROR", "هذا النوع من الوثائق مضاف مسبقا لهذا الترشح");
        }
        documentDemande.setCandidaturePlanifiee(candidaturePlanifiee);
        documentDemande.setTypeDocument(typeDocument);
        documentDemande.setObligatoire(obligatoire);
        documentDemandeRepository.save(documentDemande);
        CandidaturePlanifiee candidaturePlanifieeUpdated = candidaturePlanifieeRepository.findById(idCandidaturePlanifiee)
                .orElseThrow(() -> new ApiException("ERROR", "الترشح المفتوح غير موجود"));
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "تم تعديل الوثيقة بنجاح");
        response.put("data", candidaturePlanifieeUpdated.getDocumentsDemandes());
        return response;
    } catch (ApiException e) {
        throw e;
    } catch (Exception e) {
        e.printStackTrace();
        throw new ApiException("ERROR", "حدث خطأ أثناء تعديل الوثيقة");
    }
}
@GetMapping("/approuverCandidaturePlanifiee/{idCp}")
@Transactional
public Map<String, Object> approuverCandidaturePlanifiee(  @PathVariable Long idCp) {

    CandidaturePlanifiee candidaturePlanifiee =
            candidaturePlanifieeRepository
                    .findById(idCp)
                    .orElseThrow(() -> new ApiException(
                            "ERROR",
                            "الترشح المفتوح غير موجود"
                    ));
        if (candidaturePlanifiee.isApprouvee()) { 
        throw new ApiException(
                "ERROR",
                "الترشح المفتوح تمت المصادقة عليه مسبقا"
        );
    }
    LocalDate now = LocalDate.now(ZoneId.of("Africa/Tunis"));

    if (candidaturePlanifiee.getDd() == null
            || candidaturePlanifiee.getDf() == null
            || candidaturePlanifiee.getDd()
                    .isAfter(candidaturePlanifiee.getDf())) {

        throw new ApiException( "ERROR", "تاريخ بداية الترشح يجب أن يكون قبل أو يساوي تاريخ النهاية"  );
    }

    if (candidaturePlanifiee.getDf().isBefore(now)) {
        throw new ApiException(
                "ERROR",
                "لا يمكن المصادقة على الترشح بعد انتهاء تاريخ الترشح"
        );
    }

    if (!Boolean.TRUE.equals(candidaturePlanifiee.isApprouvee())) {

        candidaturePlanifiee.setApprouvee(true);

        candidaturePlanifiee = candidaturePlanifieeRepository .save(candidaturePlanifiee);
    }
    Map<String, Object> response = new HashMap<>();
    response.put("action", "SUCCESS");
    response.put("message", "تمت المصادقة على الترشح بنجاح" );
    response.put("data", candidaturePlanifiee);
    return response;
}

@GetMapping("/getallParticipationsPlanifieesByCp/{refCp}")
public Map<String, Object> getallParticipationsPlanifieesByCp(
        @PathVariable String refCp) {

    CandidaturePlanifiee candidaturePlanifiee =
            candidaturePlanifieeRepository
                    .findByRefCandidaturePlanifiee(refCp)
                    .orElseThrow(() -> new ApiException(
                            "ERROR",
                            "الترشح المفتوح غير موجود"
                    ));

    List<ParticipationPlanifiee> participations =
            participationPlanifieeRepository
                    .findByCandidaturePlanifieeRefCandidaturePlanifiee(refCp);

    Map<String, Object> response = new HashMap<>();

    response.put("action", "SUCCESS");
    response.put(
            "message",
            "تم جلب قائمة المشاركين في الترشح بنجاح"
    );
    response.put("data", participations);
    response.put("candidaturePlanifiee", candidaturePlanifiee);

    return response;
}


}
