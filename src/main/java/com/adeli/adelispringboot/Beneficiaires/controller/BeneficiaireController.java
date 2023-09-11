/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Beneficiaires.controller;


//import com.example.demo.entity.Role;


import com.adeli.adelispringboot.Beneficiaires.dto.BenefReqDto;
import com.adeli.adelispringboot.Beneficiaires.entity.Beneficiaire;
import com.adeli.adelispringboot.Beneficiaires.repository.BeneficiaireRepository;
import com.adeli.adelispringboot.Beneficiaires.service.IBeneficiaireService;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Tontine.service.ITontineService;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.Users.service.IUserService;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
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

import java.time.LocalDateTime;


/**
 *
 * @author Casimir
 */
@RestController
@RequestMapping("/api/v1.0/beneficiaire")
@Tag(name = "Beneficiaire")
@Slf4j
public class BeneficiaireController {

//    @Autowired
//    TontineRepository tontineRepository;
    
    @Autowired
    ISessionRepo sessionRepository;
    
    @Autowired
    IUserRepo userRepository;
    
    @Autowired
    BeneficiaireRepository beneficiaireRepository;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;
    
    JSONObject json;
    String mts;

    @Autowired
    IUserService iUserService;
    @Autowired
    ISeanceService iSeanceService;
    @Autowired
    ITontineService iTontineService;
    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;
    @Autowired
    IBeneficiaireService iBeneficiaireService;

//    @GetMapping
//    public List<JSONObject> getTontine(){
//        List<JSONObject> p ;
//        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
//                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));
//
//        Session session = sessionRepository.findSessionByStatus(sessionStatus);
//
//        p = beneficiaireRepository.getAllBenefBySession(session.getId());
//        return p ;
//    }


    @Operation(summary = "création des informations pour un bénéficiaire", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Beneficiaire.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> newBenef(@RequestBody BenefReqDto benefReqDto) {
        Tontine tontine = new Tontine();
        Beneficiaire beneficiaire = new Beneficiaire();
        Users user = iUserService.getById(benefReqDto.getIdUser());
        Seance seance = iSeanceService.getById(benefReqDto.getIdSeance());
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(EStatusTransaction.RETRAIT).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));


        if(iTontineService.soldeTontine() < benefReqDto.getMontant()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.insufficient_amount", null, LocaleContextHolder.getLocale())));
        }

        tontine.setDescription(user.getLastName()+": a bouffé");
        tontine.setUser(user);
        tontine.setSeance(seance);
        tontine.setCreatedAt(LocalDateTime.now());
        tontine.setTypeTransaction(typeTransaction);
        tontine.setMontant(benefReqDto.getMontant());


        beneficiaire.setCreatedAt(LocalDateTime.now());
        beneficiaire.setUser(user);
        beneficiaire.setSeance(seance);
        beneficiaire.setMontant(benefReqDto.getMontant());


        iBeneficiaireService.createBeneficiaire(beneficiaire);
        iTontineService.createTontine(tontine);
      return ResponseEntity.ok(beneficiaire);
    }


    @Operation(summary = "création des informations pour un bénéficiaire", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Beneficiaire.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> updateBenef(@RequestBody BenefReqDto benefReqDto, @PathVariable Long id) {
        Beneficiaire beneficiaire = iBeneficiaireService.getBeneficiaire(id);
        Users user = iUserService.getById(benefReqDto.getIdUser());
        Tontine tontine = iTontineService.getTontineByDescriptionAndSeance(beneficiaire.getSeance(), beneficiaire.getUser().getLastName()+": a bouffé");

        if((iTontineService.soldeTontine() + beneficiaire.getMontant()) < benefReqDto.getMontant()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.insufficient_amount", null, LocaleContextHolder.getLocale())));
        }

        tontine.setDescription(user.getLastName()+": a bouffé");
        tontine.setUser(user);
        tontine.setUpdatedAt(LocalDateTime.now());
        tontine.setMontant(benefReqDto.getMontant());


        beneficiaire.setUpdatedAt(LocalDateTime.now());
        beneficiaire.setUser(user);
        beneficiaire.setMontant(benefReqDto.getMontant());


        iBeneficiaireService.createBeneficiaire(beneficiaire);
        iTontineService.createTontine(tontine);
      return ResponseEntity.ok(beneficiaire);
    }


    @Operation(summary = "création des informations pour un bénéficiaire", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Beneficiaire.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> deleteBenef(@PathVariable Long id) {
        Beneficiaire beneficiaire = iBeneficiaireService.getBeneficiaire(id);
        Tontine tontine = iTontineService.getTontineByDescriptionAndSeance(beneficiaire.getSeance(), beneficiaire.getUser().getLastName()+": a bouffé");

        iBeneficiaireService.deleteBeneficiaire(beneficiaire);
        iTontineService.deleteTontine(tontine);
      return ResponseEntity.ok(beneficiaire);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des beneficiaires", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("")
    public Page<Beneficiaire> getBeneficiaire(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                @RequestParam(required = false, defaultValue = "id") String sort,
                                @RequestParam(required = false, defaultValue = "desc") String order) {
        return iBeneficiaireService.getAllBeneficiaires(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des prets par seance", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/seance/{idSeance:[0-9]+}")
    public ResponseEntity<Page<Beneficiaire>> getBeneficiaireBySeance(@PathVariable Long idSeance,
                                                       @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                       @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                       @RequestParam(required = false, defaultValue = "id") String sort,
                                                       @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Beneficiaire> list = iBeneficiaireService.getBeneficiaireBySeance(idSeance, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
        
}
