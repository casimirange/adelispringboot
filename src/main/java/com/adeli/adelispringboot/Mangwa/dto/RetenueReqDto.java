package com.adeli.adelispringboot.Mangwa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Data
public class RetenueReqDto {
    @Schema(required = true, example = "5€", description = "montant de la transaction")
    @NotNull(message = "{date.required}")
    private double montant;

    @Schema(required = true, example = "17/12/2022", description = "date à laquelle a eu lieu la transaction")
    @NotNull(message = "{date.required}")
    private LocalDate date;

    @Schema(required = true, description = "motif de la transaction")
    @NotNull(message = "{date.required}")
    private String motif;

    @Schema(required = true, example = "17/12/2022", description = "date à laquelle a eu lieu la transaction")
    @NotNull(message = "{date.required}")
    private String transaction;

    public Long user;
}
