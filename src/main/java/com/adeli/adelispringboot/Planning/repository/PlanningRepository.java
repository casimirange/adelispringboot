/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.repository;

import com.example.demo.entity.Planing;
import com.example.demo.entity.Session;
import java.time.LocalDate;
import java.util.List;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Casimir
 */
@Repository
public interface PlanningRepository extends JpaRepository<Planing, Long> {
    Boolean existsByDate(LocalDate date);
    Boolean existsByDateAndEtat(LocalDate date, Boolean b);
    Long countBySession(Session session);
    List<JSONObject> findDistinctDateBySessionOrderByDateAsc(Session session);
    
    String planing = "select p.id_planning as id, p.date, p.evenement, u.name as membre, u.id as id_user from planing p "
            + "join session s on p.id_session = s.id_session "
            + "join user u on u.id = p.id_user "
            + "where s.etat = 1"
            + " ORDER by p.date ASC ";
  
    @Query(value=planing, nativeQuery = true)
    public List<JSONObject> findPlaning();
}