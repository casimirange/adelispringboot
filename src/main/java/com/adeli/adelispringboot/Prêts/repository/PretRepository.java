/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Prêts.repository;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Discipline.entity.Discipline;
import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Users.entity.Users;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Casimir
 */
@Repository
public interface PretRepository extends JpaRepository<Prets, Long> {
//    List<Prets> findByDatePret(LocalDate date);
//    List<Prets> findByRembourse(boolean etat);
//    List<Prets> findByUser(Users user);
    Page<Prets> findBySeance(Seance seance, Pageable pageable);
    Page<Prets> findAll(Specification<Prets> specification, Pageable pageable);
    
    String planing = "select p.id_pret as id, p.id_pret, p.date_pret, p.date_remboursement, p.montant_prete, p.montant_rembourse,"
            + "p.rembourse, s.taux, u.name as nom from prets p "
            + "join session s on p.id_session = s.id_session "
            + "join user u on u.id = p.id_user "
            + "where s.etat = 1"
            + " ORDER by p.date_pret desc ";
  
    @Query(value=planing, nativeQuery = true)
    public List<JSONObject> findPrets();
    
    String pretUser = "select p.id_pret as id, p.id_pret, p.date_pret, p.date_remboursement, p.montant_prete, p.montant_rembourse,"
            + "p.rembourse, s.taux, u.name as nom from prets p "
            + "join session s on p.id_session = s.id_session "
            + "join user u on u.id = p.id_user "
            + "where u.id = ?1"
            + " ORDER by p.date_pret desc ";
  
    @Query(value=pretUser, nativeQuery = true)
    public List<JSONObject> findPretsUser(Long id);
}