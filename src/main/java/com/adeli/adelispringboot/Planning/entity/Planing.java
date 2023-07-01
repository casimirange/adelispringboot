/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Planning.entity;

import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

/**
 *
 * @author Casimir
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Planing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlanning;
 
//    @NotBlank
    private LocalDate date;
 
//    @NotBlank
    private boolean etat;
 
//    @NotBlank
    private String evenement;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idSession")
    public Session session;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idUser")
    public Users user;

}
