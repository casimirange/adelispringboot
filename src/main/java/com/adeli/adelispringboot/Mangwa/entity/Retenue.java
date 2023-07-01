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
public class Retenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRetenue;
 
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
    private String transaction;
    
    @ManyToOne(fetch = FetchType.EAGER) //plusieurs lignes pour un département
	@JoinColumn(name = "idUser")
    public User user;

    public Retenue(double debit, double credit, double solde, LocalDate date, String motif, String trans, User user) {
        this.debit = debit;
        this.credit = credit;
        this.solde = solde;
        this.date = date;
        this.motif = motif;
        this.transaction = trans;
        this.user = user;
    }

    public Retenue() {
    }

    public Long getIdRetenue() {
        return idRetenue;
    }

    public void setIdRetenue(Long idRetenue) {
        this.idRetenue = idRetenue;
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

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

}
