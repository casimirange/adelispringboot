/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Discipline.repository;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Discipline.entity.Discipline;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    Page<Discipline> findBySeance(Seance seance, Pageable pageable);
    Page<Discipline> findAll(Specification<Discipline> specification, Pageable pageable);
}