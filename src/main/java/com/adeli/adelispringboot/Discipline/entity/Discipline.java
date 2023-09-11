/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Discipline.entity;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Discipline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private TypeDiscipline typeDiscipline;

    private String sanction;
    
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idUser")
    public Users user;

    @ManyToOne
    @JoinColumn(name = "id_seance")
    @JsonIgnore
    private Seance seance;

    private LocalDateTime createdAt;

    private LocalDate date;

    private LocalDateTime updatedAt;

}
