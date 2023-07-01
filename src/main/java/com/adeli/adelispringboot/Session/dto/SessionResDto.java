package com.adeli.adelispringboot.Session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class SessionResDto {

    @Schema(required = true, example = "5 €", description = "montant destiné au mangwa pour chaque utilisateur")
    @NotNull(message = "{mangwa.required}")
    private double mangwa;

    @NotNull(message = "{beginDate.required}")
    private LocalDate beginDate;

    @NotNull(message = "{endDate.required}")
    private LocalDate endDate;

    @Schema(required = true, example = "1.5", description = "nom de celui qui crée la session")
    @NotNull(message = "{creator.required}")
    private String creator;

    @Schema(required = true, example = "1.5", description = "taux appliqué aux prêts durant la session")
    @NotNull(message = "{tax.required}")
    private double tax;

}
