/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Mangwa.repository;

import com.adeli.adelispringboot.Mangwa.entity.Retenue;
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
public interface RetenueRepository extends JpaRepository<Retenue, Long> {
    Retenue findFirstByOrderByIdRetenueDesc();
    
    String allMangwa = "select r.id_retenue, r.date, r.motif, r.credit, r.debit,"
            + "r.solde, u.name as nom from retenue r "
            + "join user u on u.id = r.id_user "
            + "ORDER by r.id_retenue desc ";
  
    @Query(value=allMangwa, nativeQuery = true)
    public List<JSONObject> findMangwa();
}