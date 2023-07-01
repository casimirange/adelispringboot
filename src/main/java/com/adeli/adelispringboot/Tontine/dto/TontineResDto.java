package com.adeli.adelispringboot.Tontine.dto;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class TontineReqDto {
    @Schema(required = true, example = "500 €", description = "montant de la cotisation")
    @NotNull(message = "{montant.required}")
    private double montant;

    @Schema(required = true, description = "membre qui cotise")
    @NotNull(message = "{users.required}")
    private Long users;

    @Schema(required = true, description = "motif de la transaction")
    @NotNull(message = "{description.required}")
    private String description;

    @Schema(required = true, description = "séance à laquelle s'est tenue la réunion")
    @NotNull(message = "{seance.required}")
    private Long seance;

    @Schema(required = true, defaultValue = "{DEPOT}", allowableValues = "{DEPOT, RETRAIT, PRET, REMBOURSEMENT}")
    @NotNull(message = "{typeTransaction.required}")
    private Long typeTransaction;
}
