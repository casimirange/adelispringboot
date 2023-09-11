package com.adeli.adelispringboot.Prêts.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PretRembReqDto {

    double montant_rembourse;

    Long idSeance;

    boolean total;
}
