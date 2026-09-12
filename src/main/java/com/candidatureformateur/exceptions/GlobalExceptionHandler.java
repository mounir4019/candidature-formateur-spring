package com.candidatureformateur.exceptions;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * ============================================================
     * Exceptions métier
     * ============================================================
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(
            ApiException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "action", ex.getAction(),
                        "message", ex.getMessageApi()
                ));
    }


    /*
     * ============================================================
     * Sécurité : 403
     * ============================================================
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "action", "FORBIDDEN",
                        "message", "تم رفض الوصول."
                ));
    }


    /*
     * ============================================================
     * Toutes les autres exceptions
     * ============================================================
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(
            Exception ex) {

        System.err.println(
                "Erreur inattendue : " + ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "action", "ERROR",
                        "message", "حدث خطأ غير متوقع."
                ));
    }
}
    // Gestion des exceptions métier personnalisées
/*     @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getBody());
    }
    // Sécurité (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "action", "FORBIDDEN",
                        "message", Map.of(
                                "fr", "Accès refusé.",
                                "ar", "تم رفض الوصول.",
                                "en", "Access denied."
                        )
                ));
    }
    // Gestion de toutes les autres exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
        Map<String, Object> genericError = Map.of(
                "action", "ERROR",
                "message", Map.of(
                        "fr", "Une erreur est survenue.",
                        "ar", "حدث خطأ.",
                        "en", "An error occurred."
                )
        );
        System.err.println("Erreur inattendue: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericError);
    } */
 
