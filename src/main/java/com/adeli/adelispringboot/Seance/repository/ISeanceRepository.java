/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Seance.repository;

import com.adeli.adelispringboot.CompteRendu.entity.CompteRendu;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Casimir
 */
@Repository
public interface CompteRenduRepository extends JpaRepository<CompteRendu, Long> {
    String cr = "select c.id_compte_rendu, c.date, c.details from compte_rendu c "
            + "join session s on c.id_session = s.id_session "
            + "where s.etat = 1"
            + " ORDER by c.date desc ";
  
    @Query(value=cr, nativeQuery = true)
    public List<JSONObject> findCR();
}