/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Amandes.repository;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
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
public interface IAmandeRepo extends JpaRepository<Amande, Long> {
//    List<Amande> findByDate(LocalDate date);
//    List<Amande> findByUser(Users user);
//    Amande findFirstByOrderByIdAmandeDesc();
    Page<Amande> findBySeance(Seance seance, Pageable pageable);
    Page<Amande> findAll(Specification<Amande> specification, Pageable pageable);

//    String amande = "select a.id_amande, a.date, a.credit, a.debit, a.motif, a.solde, "
//            + "u.name from amande a "
//            + "join session s on a.id_session = s.id_session "
//            + "join user u on u.id = a.id_user "
//
//            + "ORDER by a.date desc limit 0,10 ";
//
//    @Query(value=amande, nativeQuery = true)
//    public List<JSONObject> findAmande();
//
//    String amandeUser = "select a.id_amande, a.date, a.credit, a.debit, a.motif, a.solde, "
//            + "u.name from amande a "
//            + "join session s on a.id_session = s.id_session "
//            + "join user u on u.id = a.id_user "
//            + "where u.id = ?1 "
//            + "ORDER by a.date desc ";
//
//    @Query(value=amandeUser, nativeQuery = true)
//    public List<JSONObject> findAmandeUser(Long id);
}