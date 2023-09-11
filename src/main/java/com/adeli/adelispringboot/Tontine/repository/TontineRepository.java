/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Tontine.repository;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Casimir
 */
@Repository
public interface TontineRepository extends JpaRepository<Tontine, Long> {
//    List<Tontine> findByDate(LocalDate date);
//    List<Tontine> findBySession(Session session);
    Tontine findFirstByOrderByIdTontineDesc();
    Tontine findTontineBySeanceAndDescription(Seance seance, String description);
    Tontine findTontineBySeanceAndDescriptionAndMontant(Seance seance, String description, Double montant);
    Page<Tontine> findBySeance(Seance seance, Pageable pageable);
    Boolean existsBySeanceAndTypeTransaction(Seance seance, TypeTransaction typeTransaction);
//    Integer countByDate(LocalDate date);

    String solde = "SELECT SUM(c.montant) as montant, t.name FROM tontine c INNER JOIN status_transaction t on t.id = c.type_transaction_id GROUP BY c.type_transaction_id";

    @Query(value=solde, nativeQuery = true)
    public List<JSONObject> soldeTontine();
    
//    String tontineDuMois = "SELECT t.date, t.motif, t.credit, t.debit, t.montant, u.name\n" +
//        "from tontine t\n" +
//        "join user u on (u.id = t.id_user) \n" +
//        "where date_format(t.date, '%Y/%m') = ?1 ";
//
//    @Query(value=tontineDuMois, nativeQuery = true)
//    public List<JSONObject> Tontine(String month);
    
//    String tontineSession = "SELECT t.date, t.motif, t.credit, t.debit, t.montant, u.name\n" +
//        "from tontine t\n" +
//        "join user u on (u.id = t.id_user) \n" +
//        "join session s on s.id_session = t.id_session \n" +
//        "where t.id_session = ?1 \n"+
//        "order by t.id_tontine desc";
//
//    @Query(value=tontineSession, nativeQuery = true)
//    public List<JSONObject> TontineSession(Long id);
    
//    String tontineUser = "SELECT t.date, t.motif, t.credit, t.debit, t.montant, u.id\n" +
//        "from tontine t\n" +
//        "join user u on (u.id = t.id_user) \n" +
//        "where u.id = ?1 \n"+
//        "order by t.id_tontine desc";
//
//    @Query(value=tontineUser, nativeQuery = true)
//    public List<JSONObject> TontineUser(Long id);

}