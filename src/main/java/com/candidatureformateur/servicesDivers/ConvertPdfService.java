package com.candidatureformateur.servicesDivers;

import java.io.ByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.util.List;
import java.io.ByteArrayInputStream;
 // import java.io.ByteArrayOutputStream;
public class ConvertPdfService {
    // Méthode pour convertir une image en PDF
    public static byte[] convertImageToPDF(MultipartFile imageFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Convertir l'image en un objet PDFBox
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageFile.getBytes(), imageFile.getOriginalFilename());
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Ajuster l'image à la taille de la page PDF
            contentStream.drawImage(pdImage, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
            contentStream.close();

            // Convertir le PDF en tableau d'octets (byte[])
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);

            return outputStream.toByteArray();
        }

}
 // Méthode pour fusionner les PDF et images dans un seul fichier PDF
    public static byte[] mergePDFs /* AndImages */(List<byte[]> pdfFiles /*,  List<byte[]> imageFiles,   List<String> imageFormats*/) throws IOException {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Ajouter tous les fichiers PDF à fusionner
        for (byte[] pdf : pdfFiles) {
            pdfMerger.addSource(new ByteArrayInputStream(pdf));
        }

        // Convertir chaque image en PDF et les ajouter au merge
       /*  for (int i = 0; i < imageFiles.size(); i++) {
            byte[] image = imageFiles.get(i);
            String format = imageFormats.get(i);  // Format de l'image (ex: "jpg", "png")

            PDDocument imagePdf = convertImageToPDF(image, format);
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            imagePdf.save(imageOutputStream);
            imagePdf.close();

            pdfMerger.addSource(new ByteArrayInputStream(imageOutputStream.toByteArray()));
        } */

        // Fusionner tous les fichiers dans un seul PDF
        pdfMerger.setDestinationStream(outputStream);
        pdfMerger.mergeDocuments(null);

        return outputStream.toByteArray();
    }
}
