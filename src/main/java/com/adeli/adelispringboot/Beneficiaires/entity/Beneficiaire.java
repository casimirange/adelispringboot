/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Beneficiaires.entity;

import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Users.entity.Users;
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
@AllArgsConstructor
@NoArgsConstructor
public class Beneficiaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUser")
    public Users user;

    @ManyToOne
    @JoinColumn(name = "id_seance")
    private Seance seance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
