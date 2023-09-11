package com.adeli.adelispringboot.Prêts.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PretCreateReqDto {
    double montant_prete;

    Long idUser;

    Long idSeance;

    boolean type;
}
