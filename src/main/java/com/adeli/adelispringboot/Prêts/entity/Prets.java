/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author Casimir
 */
@Entity
public class Prets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPret;
 
//    @NotBlank
    private double montant_prete;
    
//    @NotBlank
    private double montant_rembourse;
 
//    @NotBlank
    private LocalDate datePret;
 
    private LocalDate dateRemboursement;
 
//    @NotBlank
    private boolean rembourse;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idUser")
    public User user;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idSession")
    public Session session;

    public Prets(double montant_prete, double montant_rembourse, LocalDate datePret, LocalDate dateRemboursement, boolean rembourse, User user, Session session) {
        this.montant_prete = montant_prete;
        this.montant_rembourse = montant_rembourse;
        this.datePret = datePret;
        this.dateRemboursement = dateRemboursement;
        this.rembourse = rembourse;
        this.user = user;
        this.session = session;
    }

    public Prets() {
    }
    

    public Long getIdPret() {
        return idPret;
    }

    public void setIdPret(Long idPret) {
        this.idPret = idPret;
    }

    public double getMontant_prete() {
        return montant_prete;
    }

    public void setMontant_prete(double montant_prete) {
        this.montant_prete = montant_prete;
    }

    public double getMontant_rembourse() {
        return montant_rembourse;
    }

    public void setMontant_rembourse(double montant_rembourse) {
        this.montant_rembourse = montant_rembourse;
    }

    public LocalDate getDatePret() {
        return datePret;
    }

    public void setDatePret(LocalDate datePret) {
        this.datePret = datePret;
    }

    public LocalDate getDateRemboursement() {
        return dateRemboursement;
    }

    public void setDateRemboursement(LocalDate dateRemboursement) {
        this.dateRemboursement = dateRemboursement;
    }

    public boolean isRembourse() {
        return rembourse;
    }

    public void setRembourse(boolean rembourse) {
        this.rembourse = rembourse;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    
}
