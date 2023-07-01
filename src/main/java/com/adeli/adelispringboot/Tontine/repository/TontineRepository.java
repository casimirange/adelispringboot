/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.repository;

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
public interface TontineRepository extends JpaRepository<Tontine, Long> {
    List<Tontine> findByDate(LocalDate date);
    List<Tontine> findBySession(Session session);
    Tontine findFirstByOrderByIdTontineDesc();
    Boolean existsByDateAndUser(LocalDate date, User user);
    Integer countByDate(LocalDate date);
    
    String tontineDuMois = "SELECT t.date, t.motif, t.credit, t.debit, t.montant, u.name\n" +
        "from tontine t\n" +
        "join user u on (u.id = t.id_user) \n" +
        "where date_format(t.date, '%Y/%m') = ?1 ";
  
    @Query(value=tontineDuMois, nativeQuery = true)
    public List<JSONObject> Tontine(String month);
    
    String tontineSession = "SELECT t.date, t.motif, t.credit, t.debit, t.montant, u.name\n" +
        "from tontine t\n" +
        "join user u on (u.id = t.id_user) \n" +
        "join session s on s.id_session = t.id_session \n" +
        "where t.id_session = ?1 \n"+
        "order by t.id_tontine desc";
  
    @Query(value=tontineSession, nativeQuery = true)
    public List<JSONObject> TontineSession(Long id);
    
    String tontineUser = "SELECT t.date, t.motif, t.credit, t.debit, t.montant, u.id\n" +
        "from tontine t\n" +
        "join user u on (u.id = t.id_user) \n" +
        "where u.id = ?1 \n"+
        "order by t.id_tontine desc";
  
    @Query(value=tontineUser, nativeQuery = true)
    public List<JSONObject> TontineUser(Long id);

}