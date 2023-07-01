/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.repository;

import com.example.demo.entity.Beneficiaire;
import com.example.demo.entity.Reunion;
import com.example.demo.entity.Session;
import com.example.demo.entity.Tontine;
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
public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {
    List<Beneficiaire> findByDate(LocalDate date);
    Boolean existsBySessionAndNom(Session session, String nom);
    Beneficiaire findFirstByOrderByIdBeneficiaireDesc();
    
    String benefSession = "SELECT b.id_beneficiaire, b.date, b.montant, b.nom\n" +
        "from beneficiaire b\n" +
        "join session s on s.id_session = b.id_session \n" +
        "where b.id_session = ?1 \n"+
        "order by b.date desc";
  
    @Query(value=benefSession, nativeQuery = true)
    public List<JSONObject> getAllBenefBySession(Long id);
}