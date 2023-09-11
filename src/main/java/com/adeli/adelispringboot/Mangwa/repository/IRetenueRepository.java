/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Mangwa.repository;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Seance.entity.Seance;
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
public interface IRetenueRepository extends JpaRepository<Retenue, Long> {
    Retenue findFirstByOrderByIdDesc();

    Page<Retenue> findByDate(LocalDate seance, Pageable pageable);

    Page<Retenue> findAll(Specification<Retenue> specification, Pageable pageable);
    
    String solde = "SELECT SUM(r.montant) as montant, t.name FROM retenue r INNER JOIN status_transaction t on t.id = r.type_transaction_id GROUP BY r.type_transaction_id";
  
    @Query(value=solde, nativeQuery = true)
    public List<JSONObject> soldeMangwa();

    Retenue findRetenueByMotifAndMontantAndDateAndTypeTransaction(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction);
}