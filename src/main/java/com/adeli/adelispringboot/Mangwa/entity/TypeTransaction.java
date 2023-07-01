package com.adeli.adelispringboot.Mangwa.entity;

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
@Table(name = "StatusTransaction")
public class TypeTransaction {

    @Schema(description = "identifiant unique du statut de la transaction", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Schema(description = "Statut de la transaction", example = "DEPOT, RETRAIT")
    @Enumerated(EnumType.STRING)
    private EStatusTransaction name;

    public TypeTransaction(EStatusTransaction name) {
        this.name = name;
    }
}
