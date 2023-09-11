/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Tontine.controller;

import com.adeli.adelispringboot.Document.entity.ETypeDocument;
import com.adeli.adelispringboot.Document.entity.TypeDocument;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IRetenueRepository;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Mangwa.service.IMangwaService;
import com.adeli.adelispringboot.Planning.repository.PlanningRepository;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.repository.ISeanceRepository;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Session.service.ISessionService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Tontine.repository.TontineRepository;
import com.adeli.adelispringboot.Tontine.service.ITontineService;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.Users.service.IUserService;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
import com.adeli.adelispringboot.configuration.email.dto.EmailDto;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/**
 * @author Casimir
 */

@RestController
@Tag(name = "Tontine")
@RequestMapping("/api/v1.0/tontine")
@Slf4j
public class TontineController {

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;

    @Autowired
    TontineRepository tontineRepository;

    @Autowired
    ISessionRepo sessionRepository;

    @Autowired
    IUserRepo userRepository;

    @Autowired
    IRetenueRepository IRetenueRepository;

    @Autowired
    PlanningRepository planningRepository;

    @Autowired
    ISeanceService iSeanceService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ITontineService iTontineService;

    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    @Autowired
    IMangwaService iMangwaService;

    @Autowired
    ISessionService iSessionService;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Operation(summary = "création des informations pour une tontine", tags = "Tontine", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Tontine.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> createTontine(@RequestParam("seance") Long idSeance) {
        List<Users> usersList = iUserService.getUsers();
        Seance seance = iSeanceService.getById(idSeance);
        List<Tontine> tontines = new ArrayList<>();
        List<Retenue> retenueList = new ArrayList<>();
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(EStatusTransaction.DEPOT).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));
        if (!iTontineService.existByDate(seance, typeTransaction)){
            for (Users user : usersList) {
                if (user.getStatus().getName() == EStatusUser.USER_ENABLED) {
                    Tontine tontine = new Tontine();
                    Retenue retenue = new Retenue();
                    Session session = iSessionService.findLastSession();
                    if (session.getStatus().getName() == EStatusSession.CREEE){
                        retenue.setMontant(session.getMangwa());
                    }else {
                        return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                                messageSource.getMessage("messages.session_exists", null, LocaleContextHolder.getLocale())));
                    }

                    tontine.setSeance(seance);
                    tontine.setTypeTransaction(typeTransaction);
                    tontine.setCreatedAt(LocalDateTime.now());

                    tontines.add(tontine);

                    retenue.setTypeTransaction(typeTransaction);
                    retenue.setDate(seance.getDate());
                    retenue.setMotif("contribution mangwa");
                    retenue.setCreatedAt(LocalDateTime.now());
                    tontine.setUser(user);
                    tontine.setMontant(user.getMontant());
                    tontine.setDescription(user.getLastName() + " a cotisé");
                    retenue.setUser(user);

                    retenueList.add(retenue);
                }
            }
            iTontineService.createTontines(tontines);
            iMangwaService.createMangwas(retenueList);
        }else {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.cotisation", null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.ok().body(tontines);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des cotisations par seance", tags = "Tontine", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/seance/{idSeance:[0-9]+}")
    public ResponseEntity<Page<TontineResDto>> getTontineBySeance(@PathVariable Long idSeance,
                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                  @RequestParam(required = false, defaultValue = "idTontine") String sort,
                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<TontineResDto> list = iTontineService.getTontinesBySeance(idSeance, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des cotisations", tags = "Tontine", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping()
    public ResponseEntity<Page<Tontine>> getTontines( @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                      @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                      @RequestParam(required = false, defaultValue = "idTontine") String sort,
                                                      @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Tontine> list = iTontineService.getAllTontine(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Solde tontine", tags = "Tontine", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Mangwa not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("/solde")
    public ResponseEntity<?> soldeTontine() {
        double solde = iTontineService.soldeTontine();
        return ResponseEntity.ok(solde);
    }

//    @GetMapping("/session/{id}")
//    public List<JSONObject> getSessionTontine(@PathVariable Long id) {
//        return tontineRepository.TontineSession(id);
//    }

//    @GetMapping("/session")
//    public List<JSONObject> getActiveSessionTontine() {
//        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
//                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));
//
//        Session session = sessionRepository.findSessionByStatus(sessionStatus);
//        List<JSONObject> p;
//
//        p = tontineRepository.TontineSession(session.getId());
//
//        return p;
//    }


//    @GetMapping("/id/{id}")
//    public List<JSONObject> getUsers(@PathVariable Long id) {
//        return tontineRepository.TontineUser(id);
//    }

}
