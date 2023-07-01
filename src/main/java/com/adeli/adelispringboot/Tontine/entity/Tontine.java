/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Tontine.entity;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Casimir
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tontine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTontine;

    private double montant;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_seance")
    @JsonIgnore
    private Seance seance;

    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idUser")
    public Users user;

    @ManyToOne
    private TypeTransaction typeTransaction;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
