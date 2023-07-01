package com.adeli.adelispringboot.Tontine.dto;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Users.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TontineResDto {

    private Long id;

    @Schema(required = true, example = "500 €", description = "montant de la cotisation")
    private double montant;

    @Schema(required = true, description = "membre qui cotise")
    private Users user;

    @Schema(required = true, description = "membre qui cotise")
    private String name;

    @Schema(required = true, description = "motif de la transaction")
    private String description;

    @Schema(required = true, description = "séance à laquelle s'est tenue la réunion")
    private Seance seance;

    @Schema(required = true, defaultValue = "{DEPOT}", allowableValues = "{DEPOT, RETRAIT, PRET, REMBOURSEMENT}")
    private TypeTransaction typeTransaction;

    @Schema(required = true, defaultValue = "{DEPOT}", allowableValues = "{DEPOT, RETRAIT, PRET, REMBOURSEMENT}")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
