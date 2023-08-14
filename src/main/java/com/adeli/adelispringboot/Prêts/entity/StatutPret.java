package com.adeli.adelispringboot.Prêts.entity;

import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
@Table(name = "StatusPret")
public class StatutPret {
    @Schema(description = "identifiant unique du statut du prêt", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Schema(description = "Statut de la transaction", example = "EN_COURS, INNACHEVE, TERMINE")
    @Enumerated(EnumType.STRING)
    private EStatusPret name;

    public StatutPret(EStatusPret name) {
        this.name = name;
    }
}
