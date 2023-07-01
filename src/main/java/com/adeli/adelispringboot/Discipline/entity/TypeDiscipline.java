package com.adeli.adelispringboot.Session.entity;


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
@Table(name = "StatusSession")
public class SessionStatus {

    @Schema(description = "identifiant unique du statut de la session", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Schema(description = "Statut de la session", example = "CREEE, TERMINEE")
    @Enumerated(EnumType.STRING)
    private EStatusSession name;

    public SessionStatus(EStatusSession name) {
        this.name = name;
    }
}
