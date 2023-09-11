/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Prêts.entity;

import com.adeli.adelispringboot.Discipline.entity.TypeDiscipline;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Null;
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
public class Prets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPret;
 
//    @NotBlank
    private double montant_prete;

    private double montant_rembourse;
 
    private LocalDate dateRemboursement;

    @ManyToOne
    private TypeTransaction typeTransaction;

    @ManyToOne
    private StatutPret statutPret;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUser")
    public Users user;

    @ManyToOne
    @JoinColumn(name = "id_seance")
    private Seance seance;

    private LocalDateTime createdAt;
    private LocalDate date;

    private LocalDateTime updatedAt;

}
