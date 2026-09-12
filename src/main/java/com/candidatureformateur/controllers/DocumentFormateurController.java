package com.candidatureformateur.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.candidatureformateur.entity.DocumentFormateur;
import com.candidatureformateur.entity.Formateur;
import com.candidatureformateur.entity.TypeDocument;
import com.candidatureformateur.exceptions.ApiException;
import com.candidatureformateur.repository.DocumentFormateurRepository;
import com.candidatureformateur.repository.DocumentParticipationPlanifieeRepository;
import com.candidatureformateur.repository.FormateurRepository;
import com.candidatureformateur.repository.TypeDocumentRepository;
import com.candidatureformateur.servicesDivers.ConvertPdfService;
import com.candidatureformateur.servicesDivers.ServiceDivers;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/candidatureFormateurs/documentFormateurs")
@AllArgsConstructor 
public class DocumentFormateurController {
  private static final List<String> IMAGE_MIME_TYPES = Arrays.asList("image/jpeg","image/jpg", "image/png", "image/gif");
        
  private final TypeDocumentRepository typeDocumentRepository;
  private final FormateurRepository formateurRepository;
  private final DocumentFormateurRepository documentFormateurRepository;
      private final DocumentParticipationPlanifieeRepository documentParticipationPlanifieeRepository;

  

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
    @PostMapping("ajouterDocumentFormateur/{idFormateur}")
    public Map<String, Object> ajouterDocumentFormateur( 
            @RequestParam(value = "file", required = false) MultipartFile  file,
            @RequestParam("typeDocumentId") Long typeDocumentId,
            @RequestParam("typeFichier") String typeFichier,
            @PathVariable Long idFormateur
             )  throws ParseException, IOException{
                 
              Formateur formateur = formateurRepository
            .findById(idFormateur)
            .orElseThrow(() -> new ApiException( 
                    "ERROR",
                   "المكوّن غير موجود."
             ));

    TypeDocument typeDocument = typeDocumentRepository
            .findById(typeDocumentId)
            .orElseThrow(() -> new ApiException( 
                    "ERROR",
                    "نوع الوثيقة غير موجود."
             ));      
                long nombreDocuments = documentFormateurRepository
            .countByFormateurIdAndTypeDocumentId(
                    idFormateur,
                    typeDocumentId
            );

        if (nombreDocuments >= typeDocument.getNbDocAutorise()) {

                throw new ApiException(
                        "ERROR",
                        "لقد تم تجاوز العدد المسموح به من هذه الوثائق."
                );
        }
           DocumentFormateur documentFormateur= new DocumentFormateur() ;
           documentFormateur.setRefDocumentFormateur(ServiceDivers.generateRef("DF")); 
           documentFormateur.setTypeDocument(typeDocument);
           documentFormateur.setNomFichier(file.getOriginalFilename());
           documentFormateur.setDateCreation(LocalDateTime.now(ZoneId.of("Africa/Tunis")));
          // documentFormateur.setsetTypeFichier(typeFichier);
            
           if (file != null && !file.isEmpty()) {
            //documentCandidat.setFile(file.getBytes());
            //byte[] fileContent;
            String mimeType = file.getContentType();

            if (mimeType != null && IMAGE_MIME_TYPES.contains(mimeType)) {
                // Si c'est une image, la convertir en PDF
                documentFormateur.setFichier( ConvertPdfService.convertImageToPDF(file));
            } else {
                // Si c'est un PDF, récupérer directement le contenu
                documentFormateur.setFichier(file.getBytes());
            }
           }  
        //   Formateur  formateur=formateurRepository.findById(idFormateur).orElse(null); 
            documentFormateur.setFormateur( formateur); 
            DocumentFormateur saved =
            documentFormateurRepository.save(documentFormateur);
         return Map.of(
            "action", "SUCCESS",
            "message","تم اضافة وثيقة بنجاح",
            "formateur", saved.getFormateur()
          );
    }
  @PutMapping("modifierDocumentFormateur/{idDocument}")
public Map<String, Object> modifierDocumentFormateur(
        @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam("typeDocumentId") Long typeDocumentId,
       // @RequestParam("typeFichier") String typeFichier,
        @PathVariable Long idDocument
) throws IOException { 
    DocumentFormateur documentFormateur = documentFormateurRepository
            .findById(idDocument)
            .orElseThrow(() -> new ApiException( 
                    "ERROR",
                    "الوثيقة غير موجودة."
            ));

    TypeDocument typeDocument = typeDocumentRepository
            .findById(typeDocumentId)
            .orElseThrow(() -> new ApiException( 
                      "ERROR",
                    "نوع الوثيقة غير موجود."
             ));

    // Mise à jour du type
    documentFormateur.setTypeDocument(typeDocument);

    // Mise à jour du fichier uniquement si un nouveau fichier est fourni
    if (file != null && !file.isEmpty()) {

        documentFormateur.setNomFichier(file.getOriginalFilename());

        String mimeType = file.getContentType();

        if (mimeType != null && IMAGE_MIME_TYPES.contains(mimeType)) {

            documentFormateur.setFichier(
                    ConvertPdfService.convertImageToPDF(file)
            );

        } else {

            documentFormateur.setFichier(file.getBytes());
        }
    }

    // Si tu utilises typeFichier dans ton entité
    // documentFormateur.setTypeFichier(typeFichier);
    documentFormateur.setDateDerniereModification(LocalDateTime.now(ZoneId.of("Africa/Tunis")));
    DocumentFormateur saved =
            documentFormateurRepository.save(documentFormateur);

        // IMPORTANT :
    // on recharge explicitement le Formateur
    Long idFormateur = documentFormateur.getFormateur().getId();

    Formateur formateur =
            formateurRepository.findById(idFormateur).orElseThrow(null);
                  /*   .orElseThrow(() -> new ApiException(Map.of(
                            "action", "ERROR",
                            "message", "المكوّن غير موجود."
                    ))) */;

    return Map.of(
            "action", "SUCCESS",
            "message", "تم تحيين الوثيقة بنجاح",
            "documentsFormateur", formateur.getDocumentsFormateur()
    );
}   
   @GetMapping("/loadPdfFile/{idDocument}")
    public ResponseEntity<byte[]> loadPdfFile(@PathVariable Long idDocument) {
        // Exemple de récupération de la session, remplacez-le par votre logique réelle
        DocumentFormateur documentFormateur= documentFormateurRepository.findById(idDocument).orElse(null);
        if (documentFormateur == null || documentFormateur.getFichier() == null) {
            return ResponseEntity.notFound().build();
        } 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("document.pdf")
                .build());

        return new ResponseEntity<>(documentFormateur.getFichier(), headers, HttpStatus.OK);
    }
@DeleteMapping("supprimerDocumentFormateur/{id}")
public Map<String, Object> supprimerDocument(@PathVariable Long id) {

    // Vérifier que le document existe
    DocumentFormateur document = documentFormateurRepository.findById(id)
            .orElseThrow(() -> new ApiException( 
                      "ERROR",
                     "الوثيقة غير موجودة."
            ));

    // Vérifier si le document est utilisé dans une participation planifiée
    boolean utilise = documentParticipationPlanifieeRepository
            .existsByDocumentFormateurId(id);

    if (utilise) {
        throw new ApiException( 
                 "ERROR",
                 "لا يمكن حذف الوثيقة لأنها مستعملة في ترشح مبرمج."
        );
    }

    // Suppression
    documentFormateurRepository.delete(document);
    Formateur formateur =
            formateurRepository.findById(document.getFormateur().getId()).orElseThrow(null);
    return Map.of(
            "action", "SUCCESS",
            "message", "تم حذف الوثيقة بنجاح.",
            "documentsFormateur", formateur.getDocumentsFormateur()
    );
}
}
