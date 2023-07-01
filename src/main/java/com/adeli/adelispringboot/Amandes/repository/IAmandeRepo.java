/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.repository;

import com.example.demo.entity.Amande;
import com.example.demo.entity.Reunion;
import com.example.demo.entity.Session;
import com.example.demo.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Casimir
 */
@Repository
public interface AmandeRepository extends JpaRepository<Amande, Long> {
    List<Amande> findByDate(LocalDate date);
    List<Amande> findByUser(User user);    
    Amande findFirstByOrderByIdAmandeDesc();
    
    String amande = "select a.id_amande, a.date, a.credit, a.debit, a.motif, a.solde, "
            + "u.name from amande a "
            + "join session s on a.id_session = s.id_session "
            + "join user u on u.id = a.id_user "
            
            + "ORDER by a.date desc limit 0,10 ";
  
    @Query(value=amande, nativeQuery = true)
    public List<JSONObject> findAmande();
    
    String amandeUser = "select a.id_amande, a.date, a.credit, a.debit, a.motif, a.solde, "
            + "u.name from amande a "
            + "join session s on a.id_session = s.id_session "
            + "join user u on u.id = a.id_user "
            + "where u.id = ?1 "
            + "ORDER by a.date desc ";
  
    @Query(value=amandeUser, nativeQuery = true)
    public List<JSONObject> findAmandeUser(Long id);
}