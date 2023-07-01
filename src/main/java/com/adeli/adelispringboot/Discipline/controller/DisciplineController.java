/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Discipline.controller;


//import com.example.demo.entity.Role;

import com.adeli.adelispringboot.Discipline.entity.Discipline;
import com.adeli.adelispringboot.Discipline.entity.ETypeDiscipline;
import com.adeli.adelispringboot.Discipline.entity.TypeDiscipline;
import com.adeli.adelispringboot.Discipline.repository.DisciplineRepository;
import com.adeli.adelispringboot.Discipline.repository.ITypeDisciplineRepo;
import com.adeli.adelispringboot.Discipline.service.IDisciplineService;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.ETypeAccount;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.Users.service.IUserService;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
import com.adeli.adelispringboot.configuration.email.dto.EmailDto;
import com.adeli.adelispringboot.configuration.email.service.IEmailService;
import com.adeli.adelispringboot.configuration.globalConfiguration.ApplicationConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Casimir
 */

@RestController
@Tag(name = "Discipline")
@RequestMapping("/api/v1.0/discipline")
@Slf4j
public class DisciplineController {
           
    @Autowired
    ISessionRepo sessionRepository;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;
    
    @Autowired
    IUserRepo userRepository;
    
    @Autowired
    DisciplineRepository disciplineRepository;

    @Autowired
    IUserService iUserService;
    @Autowired
    ISeanceService iSeanceService;
    @Autowired
    IDisciplineService iDisciplineService;
    @Autowired
    ITypeDisciplineRepo iTypeDisciplineRepo;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;
    @Autowired
    IEmailService emailService;
    ETypeDiscipline etyp = null;
    @Operation(summary = "création des informations pour une sanction", tags = "Discipline", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Discipline.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> createDiscipline(@RequestBody Discipline discipline, @RequestParam("user") Long userId, @RequestParam("seance") Long idSeance, @RequestParam("type") String type) {
        Users user = iUserService.getById(userId);
        Seance seance = iSeanceService.getById(idSeance);

        if (type.toUpperCase().equals(ETypeDiscipline.ABSENCE.toString())){
            etyp = ETypeDiscipline.ABSENCE;
        }

        if (type.toUpperCase().equals(ETypeDiscipline.RETARD.toString())){
            etyp = ETypeDiscipline.RETARD;
        }

        if (type.toUpperCase().equals(ETypeDiscipline.TROUBLE.toString())){
            etyp = ETypeDiscipline.TROUBLE;
        }
        TypeDiscipline typeDiscipline = iTypeDisciplineRepo.findByName(etyp).orElseThrow(()-> new ResourceNotFoundException("Type de discipline not found"));
        discipline.setTypeDiscipline(typeDiscipline);
        discipline.setSeance(seance);
        discipline.setUser(user);
        discipline.setCreatedAt(LocalDateTime.now());
        iDisciplineService.createDiscipline(discipline);

        String emailToEnableUser = "";
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("type", etyp);
        emailProps.put("date", seance.getDate());
        emailToEnableUser = user.getEmail();
        emailProps.put("name", user.getLastName());
        emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToEnableUser, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_DISCIPLINE + etyp, ApplicationConstant.TEMPLATE_EMAIL_DISCIPLINE));
        log.info("Email send successfull to user: " + emailToEnableUser);


        return ResponseEntity.ok(discipline);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des sanctions par seance", tags = "Discipline", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/seance/{idSeance:[0-9]+}")
    public ResponseEntity<Page<Discipline>> getDisciplineBySeance(@PathVariable Long idSeance,
                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                  @RequestParam(required = false, defaultValue = "id") String sort,
                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Discipline> list = iDisciplineService.getDisciplinesBySeance(idSeance, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
//    @PostMapping("")
//    public ResponseEntity<?> createAmande(@RequestBody Discipline discipline, @RequestParam("user") Users u) {
//        Users user = userRepository.findById(u.getUserId()).get();
//        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
//                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));
//        Session session = sessionRepository.findSessionByStatus(sessionStatus);
//        discipline.setSession(session);
//        discipline.setUser(user);
//        disciplineRepository.save(discipline);
//        return ResponseEntity.ok(discipline);
//    }
    
//    @PutMapping("")
//    public ResponseEntity<?> updateDiscipline(@RequestBody Discipline disc, @RequestParam("user") Users u, @RequestParam("id") Long id) {
//        Users user = userRepository.findById(u.getUserId()).get();
//        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
//                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));
//
//        Session session = sessionRepository.findSessionByStatus(sessionStatus);
//        Discipline discipline = disciplineRepository.findById(id).get();
//        discipline.setSession(session);
//        discipline.setUser(user);
//        discipline.setSanction(disc.getSanction());
//        discipline.setType(disc.getType());
//        discipline.setDate(disc.getDate());
//
//        disciplineRepository.save(discipline);
//        return ResponseEntity.ok(discipline);
//    }

    @GetMapping()
    public List<JSONObject> getActiveSessionTontine(){
        return disciplineRepository.findDiscipline();
    }
             
    @GetMapping("/id/{id}")
    public List<JSONObject> getUsers(@PathVariable Long id) {
        Users u = userRepository.findById(id).get();
        
        return disciplineRepository.findDisciplineUser(id);
    }
             
    @DeleteMapping("")
    public void deleDepartement(@RequestParam("id") Long id) {
        disciplineRepository.deleteById(id);
    }
}
