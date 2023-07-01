/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Planning.controller;

import com.adeli.adelispringboot.Planning.entity.Planing;
import com.adeli.adelispringboot.Planning.repository.PlanningRepository;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Casimir
 */
@RestController
@RequestMapping("/api/v1.0/planing")
public class PlanningController {

    @Autowired
    ISessionRepo sessionRepository;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;

    @Autowired
    PlanningRepository planningRepository;

    @Autowired
    IUserRepo userRepository;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @PostMapping("/new")
    public ResponseEntity<?> createSession(@RequestBody Planing planing, @RequestParam("user") Users u) {

        Users user = userRepository.findById(u.getUserId()).get();
        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));

        Session session = sessionRepository.findSessionByStatus(sessionStatus);
        planing.setSession(session);
        planing.setEtat(false);
        planing.setEvenement("réunion");
        planing.setUser(user);

        if (planing.getDate().isBefore(session.getBeginDate())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.date_planing_before", null, LocaleContextHolder.getLocale())));
        }

        if (planing.getDate().isAfter(session.getEndDate())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.date_planing_before", null, LocaleContextHolder.getLocale())));
        }

        if (planningRepository.existsByDate(planing.getDate())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.date_exist", null, LocaleContextHolder.getLocale())));
        }

        planningRepository.save(planing);
        return ResponseEntity.ok(planing);
    }

    @PutMapping("")
    public ResponseEntity<?> updateSession(@RequestBody Planing plan, @RequestParam("user") Users u, @RequestParam("id") Long id) {
        Users user = userRepository.findById(u.getUserId()).get();
        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));

        Session session = sessionRepository.findSessionByStatus(sessionStatus);
        Planing planing = planningRepository.findById(id).get();
        planing.setSession(session);
        planing.setEtat(false);
        planing.setEvenement("réunion");
        planing.setUser(user);
        planing.setDate(plan.getDate());
        if (planing.getDate().isBefore(session.getBeginDate())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.date_planing_before", null, LocaleContextHolder.getLocale())));
        }

        if (planing.getDate().isAfter(session.getEndDate())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.date_planing_before", null, LocaleContextHolder.getLocale())));
        }

        if (planningRepository.existsByDate(planing.getDate())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.date_exist", null, LocaleContextHolder.getLocale())));
        }

        planningRepository.save(planing);

        return ResponseEntity.ok(planing);
    }

    @GetMapping
    public List<JSONObject> getPlaning() {
        List<JSONObject> p;
        p = planningRepository.findPlaning();
        return p;
    }

    @DeleteMapping("")
    public void deleteplaning(@RequestParam("id") Long id) {
        planningRepository.deleteById(id);
    }

}
