/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Projet.repository;

import com.adeli.adelispringboot.Projet.entity.Projet;
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
public interface IProjetRepository extends JpaRepository<Projet, Long> {
    Projet findFirstByOrderByIdDesc();

    Page<Projet> findByDate(LocalDate seance, Pageable pageable);

    Page<Projet> findAll(Specification<Projet> specification, Pageable pageable);
    
    String solde = "SELECT SUM(r.montant) as montant, t.name FROM projet r INNER JOIN status_transaction t on t.id = r.type_transaction_id GROUP BY r.type_transaction_id";
  
    @Query(value=solde, nativeQuery = true)
    public List<JSONObject> soldeProjet();

//    Projet findRetenueByMotifAndMontantAndDateAndTypeTransaction(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction);
}