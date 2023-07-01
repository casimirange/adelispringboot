/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.repository;

import com.example.demo.entity.Amande;
import com.example.demo.entity.Discipline;
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
public interface DisciplineRepository extends JpaRepository<Discipline, Long> {    
    String discipline = "select d.id_discipline, d.date, d.type, d.sanction, "
            + "u.name, u.id from discipline d "
            + "join session s on d.id_session = s.id_session "
            + "join user u on u.id = d.id_user "
            + "where s.etat = 1 "
            + "ORDER by d.date desc ";
  
    @Query(value=discipline, nativeQuery = true)
    public List<JSONObject> findDiscipline();
    
    String disciplineUSER = "select d.id_discipline, d.date, d.type, d.sanction, "
            + "u.name, u.id from discipline d "
            + "join user u on u.id = d.id_user "
            + "where u.id = ?1 "
            + "ORDER by d.date desc ";
  
    @Query(value=disciplineUSER, nativeQuery = true)
    public List<JSONObject> findDisciplineUser(Long id);
}