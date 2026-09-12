package com.candidatureformateur.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.candidatureformateur.entity.DocumentFormateur;
import com.candidatureformateur.entity.Formateur;
import com.candidatureformateur.exceptions.ApiException;
import com.candidatureformateur.repository.DocumentFormateurRepository;
import com.candidatureformateur.repository.FormateurRepository;
import com.candidatureformateur.service.FormateurService;
import com.candidatureformateur.servicesDivers.ConvertPdfService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor; 
import org.springframework.util.CollectionUtils;
import java.util.Collections;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/candidatureFormateurs/formateurs")
@AllArgsConstructor 
public class FormateurController {
    private final FormateurRepository formateurRepository; 
    private final FormateurService formateurService;
    private final DocumentFormateurRepository documentFormateurRepository;

    @PostMapping("/getFormateurByCinIdUniqDateNaissance")
    public Map<String, Object> getFormateurByCinIdUniqDateNaissance(
            @RequestBody Map<String, Object> request) {

        return formateurService.rechercherParCinIdUniqEtDateNaissance(
                        (String) request.get("cin"),
                        (String) request.get("idUniq"),
                        (String) request.get("dateNaissance")
                );
 
    }
   @PostMapping("/ajouterFormateur")
    public Map<String, Object> ajouterFormateur(
            @RequestBody Formateur formateur) {

        return formateurService.ajouterFormateur(formateur);
    }  
    @PostMapping("/modifierFormateur")
    public Map<String, Object> modifierFormateur(
            @RequestBody Formateur formateur) {

        return formateurService.modifierFormateur(formateur);
    } 
   @GetMapping("/listeDocumentFormateurPdfMerge/{idFormateur}")
public ResponseEntity<byte[]> listeDocumentFormateurPdfMerge(
        @PathVariable Long idFormateur) throws IOException {

    try {

        Formateur formateur = formateurRepository
                .findById(idFormateur)
                .orElse(null);

        if (formateur == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        List<byte[]> allDocumentFormateur =
                documentFormateurRepository
                        .findFichiersByFormateurId(idFormateur);

        List<byte[]> allDocumentFormateurFiltre =
                filterNonEmptyDocuments(allDocumentFormateur);

        byte[] mergePDFs =
                ConvertPdfService.mergePDFs(
                        allDocumentFormateurFiltre
                );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData(
                "inline",
                "documentsFormateur.pdf"
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(mergePDFs);

    } catch (IOException e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }
} 
    private List<byte[]> filterNonEmptyDocuments(List<byte[]> allDocuments) {
        if (CollectionUtils.isEmpty(allDocuments)) {
            return Collections.emptyList();
        }

        return allDocuments.stream()
            .filter(doc -> doc != null && doc.length > 0)
            .collect(Collectors.toList());
    }
@GetMapping("/getFormateurByRefFormateur/{refFormateur}")
public Map<String, Object> getFormateurByRefFormateur(
        @PathVariable String refFormateur) throws IOException {

    Formateur formateur = formateurRepository
            .findByRefFormateur(refFormateur)
            .orElseThrow(() -> new ApiException(
                    "ERROR",
                    "المكوّن غير موجود"
            ));

    String photoBase64 = null;

    DocumentFormateur documentFormateur = formateur
            .getDocumentsFormateur()
            .stream()
            .filter(doc -> doc.getTypeDocument() != null)
            .filter(doc -> "PHOTO".equals(
                    doc.getTypeDocument().getCodeDocument()
            ))
            .findFirst()
            .orElse(null);

    if (documentFormateur != null
            && documentFormateur.getFichier() != null
            && documentFormateur.getFichier().length > 0) {

        PDDocument document = null;

        try {
            document = PDDocument.load(
                    documentFormateur.getFichier()
            );

            PDFRenderer renderer = new PDFRenderer(document);

            BufferedImage image = renderer.renderImageWithDPI(
                    0,
                    150
            );

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            ImageIO.write(
                    image,
                    "jpg",
                    baos
            );

            byte[] imageBytes = baos.toByteArray();

            photoBase64 = Base64.getEncoder()
                    .encodeToString(imageBytes);

        } finally {

            if (document != null) {
                document.close();
            }
        }
    }

    Map<String, Object> response = new HashMap<>();

    response.put("action", "SUCCESS");
    response.put("message", "تم العثور على المكوّن بنجاح");
    response.put("data", formateur);
    response.put("photo", photoBase64);

    return response;
} 
}
  
 
