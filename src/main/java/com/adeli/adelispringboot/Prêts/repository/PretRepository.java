/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.repository;

import com.example.demo.entity.Amande;
import com.example.demo.entity.Prets;
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
public interface PretRepository extends JpaRepository<Prets, Long> {
    List<Prets> findByDatePret(LocalDate date);
    List<Prets> findByRembourse(boolean etat);
    List<Prets> findByUser(User user);
    
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