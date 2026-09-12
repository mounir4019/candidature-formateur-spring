package com.candidatureformateur.controllers;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController;

import com.candidatureformateur.entity.CandidatureSpontanee;
import com.candidatureformateur.entity.Formateur;
import com.candidatureformateur.entity.ThemeFormation;
import com.candidatureformateur.exceptions.ApiException;
import com.candidatureformateur.repository.CandidatureSpontaneeRepository;
import com.candidatureformateur.repository.FormateurRepository;
import com.candidatureformateur.repository.ThemeFormationRepository;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/apicandidatureFormateurs/candidatureSontanee")
@AllArgsConstructor 

public class CandidatureSpontaneeController {
    
 private final CandidatureSpontaneeRepository candidatureSpontaneeRepository;
private final FormateurRepository formateurRepository;
private final ThemeFormationRepository themeFormationRepository;

 @PostMapping("/ajouterCandidatureSpontanee/{idFormateur}")
public Map<String, Object> ajouterCandidatureSpontanee(
        @RequestBody Map<String,Long>   request,
        @PathVariable Long idFormateur) {
   System.out.println("ddddddddddd ::: " );
    Formateur formateur = formateurRepository.findById(idFormateur)
            .orElseThrow(() -> new ApiException( 
                    "ERROR",
                     "المكوّن غير موجود."
            ));
 
    Long idTheme = Long.valueOf( request.get("idTheme")  );
    ThemeFormation themeFormation = themeFormationRepository
            .findById(idTheme)
            .orElseThrow(() -> new ApiException( 
                   "ERROR",
                  "موضوع التكوين غير موجود."
             ));
    if (candidatureSpontaneeRepository
        .existsByFormateurIdAndThemeFormationId(idFormateur, idTheme)) {

    throw new ApiException( 
              "ERROR",
           "لقد قمت بالتسجيل مسبقًا في موضوع التكوين هذا."
    ) ;
    }
    CandidatureSpontanee candidatureSpontanee =
            new CandidatureSpontanee();

    candidatureSpontanee.setFormateur(formateur);
    candidatureSpontanee.setThemeFormation(themeFormation);
    candidatureSpontanee.setDateSoumission(
            LocalDate.now(ZoneId.of("Africa/Tunis"))
    );

    candidatureSpontaneeRepository.save(candidatureSpontanee);

    return Map.of(
            "action", "SUCCESS",
            "candidatureSpontanees",
            candidatureSpontaneeRepository.findByFormateurId(idFormateur),
            "message", "تم تسجيل الترشح بنجاح."
    );
}

@GetMapping("/getAllCandidaturesSpontaneesByTheme/{idTheme}")
public Map<String, Object> getAllCandidaturesSpontaneesByTheme(
        @PathVariable Long idTheme) {

    List<CandidatureSpontanee> candidatures =
            candidatureSpontaneeRepository
                    .findByThemeFormationId(idTheme);

    Map<String, Object> response = new HashMap<>();

    response.put("action", "SUCCESS");

    response.put(
            "message",
            "تم جلب الترشحات التلقائية بنجاح"
    );

    response.put("data", candidatures);

    return response;
}

}
