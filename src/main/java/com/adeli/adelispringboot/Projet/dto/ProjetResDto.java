package com.adeli.adelispringboot.Projet.dto;

import com.adeli.adelispringboot.Users.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
public class ProjetResDto {
    private Long idRetenue;

    private double montant;

    private double solde;

    @Schema(required = true, example = "17/12/2022", description = "date à laquelle a eu lieu la transaction")
    @NotNull(message = "{date.required}")
    private LocalDate date;

    private String motif;

    private String transaction;

    public Users user;
    public String name;
}
