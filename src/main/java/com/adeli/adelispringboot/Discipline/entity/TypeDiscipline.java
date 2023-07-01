package com.adeli.adelispringboot.Discipline.entity;


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
@Table(name = "TypeDiscipline")
public class TypeDiscipline {

    @Schema(description = "identifiant unique du statut de la session", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Schema(description = "Statut de la session", example = "ABSENCE, RETARD, TROUBLE")
    @Enumerated(EnumType.STRING)
    private ETypeDiscipline name;

    public TypeDiscipline(ETypeDiscipline name) {
        this.name = name;
    }
}
