package com.adeli.adelispringboot.Beneficiaires.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BenefUptDto {
    private double montant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long id;

    public Long idUser;

    private Long idSeance;
}
