/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Beneficiaires.repository;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Beneficiaires.entity.Beneficiaire;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Session.entity.Session;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {
//    List<Beneficiaire> findByDate(LocalDate date);
//    Boolean existsBySessionAndNom(Session session, String nom);
//    Beneficiaire findFirstByOrderByIdBeneficiaireDesc();

    Page<Beneficiaire> findBySeance(Seance seance, Pageable pageable);
    
//    String benefSession = "SELECT b.id_beneficiaire, b.date, b.montant, b.nom\n" +
//        "from beneficiaire b\n" +
//        "join session s on s.id_session = b.id_session \n" +
//        "where b.id_session = ?1 \n"+
//        "order by b.date desc";
//
//    @Query(value=benefSession, nativeQuery = true)
//    public List<JSONObject> getAllBenefBySession(Long id);
}