package com.adeli.adelispringboot.Beneficiaires.dto;

import lombok.Data;

@Data
public class BenefReqDto {
    private double montant;

    public Long idUser;

    private Long idSeance;
}
