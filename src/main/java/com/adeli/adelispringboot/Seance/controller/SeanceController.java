/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Seance.controller;


//import com.example.demo.entity.Role;

import com.adeli.adelispringboot.CompteRendu.entity.CompteRendu;
import com.adeli.adelispringboot.CompteRendu.repository.CompteRenduRepository;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/compteRendu")
public class CompteRenduController {

    @Autowired
    ISessionRepo sessionRepository;
    
    @Autowired
    IUserRepo userRepository;

    @Autowired
    CompteRenduRepository compteRenduRepository;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;
    
    JSONObject json;
    String mts;
    
    @PostMapping("")
    public ResponseEntity<?> createCR(@RequestBody CompteRendu compteRendu) {
        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));
        Session session = sessionRepository.findSessionByStatus(sessionStatus);
        compteRenduRepository.save(compteRendu); 
        return ResponseEntity.ok(compteRendu);
    }
    

    @PutMapping("")
    public ResponseEntity<?> updateCR(@RequestBody CompteRendu communique, @RequestParam("id") Long id) {         
        CompteRendu com = compteRenduRepository.findById(id).get();
        com.setDetails(communique.getDetails());
        compteRenduRepository.save(com);
        return ResponseEntity.ok(com);
    }

    @GetMapping("")
    public List<JSONObject> getPrets(){      
        return compteRenduRepository.findCR();
    }
             
    @DeleteMapping("")
    public void deleteCommunique(@RequestParam("id") Long id) {
        compteRenduRepository.deleteById(id);
    }
        
}
