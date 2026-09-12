package com.candidatureformateur.entity.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter 
public class CandidaturePlanifieeRequest {

    private LocalDate dd;
    private LocalDate df;
    private Long idTheme;

    // getters/setters
}