package com.candidatureformateur.entity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationPlanifieeRequest {

    private Long idCandidaturePlanifiee;

    private Long idFormateur;

    private List<DocumentParticipationPlanifieeRequest> documents;
}