/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Seance.entity;

import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Casimir
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private Users users;

    @OneToMany
    private List<Tontine> tontines;

    @Column(nullable = true, name = "link_compte_rendu")
    private String linkCompteRendu;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "id_session")
    public Session session;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    private SessionStatus status;

}
