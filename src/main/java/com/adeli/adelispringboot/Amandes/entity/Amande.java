/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

/**
 *
 * @author Casimir
 */
@Entity
public class Amande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAmande;
 
//    @NotBlank
    private double debit;
 
//    @NotBlank
    private double credit;
 
//    @NotBlank
    private double solde;
 
//    @NotBlank
    private LocalDate date;
 
//    @NotBlank
    private String motif;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idUser")
    public User user;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idSession")
    public Session session;

    public Amande() {
    }

    public Amande(double debit, double credit, double solde, LocalDate date, String motif, User user, Session session) {
        this.debit = debit;
        this.credit = credit;
        this.solde = solde;
        this.date = date;
        this.motif = motif;
        this.user = user;
        this.session = session;
    }

    public Long getIdAmande() {
        return idAmande;
    }

    public void setIdAmande(Long idAmande) {
        this.idAmande = idAmande;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
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

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }
    
    

}
