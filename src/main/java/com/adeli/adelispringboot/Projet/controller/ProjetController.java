/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Projet.controller;

import com.adeli.adelispringboot.Beneficiaires.dto.BenefReqDto;
import com.adeli.adelispringboot.Beneficiaires.entity.Beneficiaire;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Projet.dto.ProjetReqDto;
import com.adeli.adelispringboot.Projet.entity.Projet;
import com.adeli.adelispringboot.Projet.repository.IProjetRepository;
import com.adeli.adelispringboot.Projet.service.IProjetService;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@Tag(name = "Projet")
@RequestMapping("/api/v1.0/projet")
@Slf4j
public class ProjetController {
    @Autowired
    ResourceBundleMessageSource messageSource;
    @Autowired
    IEmailService emailService;
    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    @Autowired
    IUserRepo userRepository;
    
    @Autowired
    IProjetRepository IRetenueRepository;
    @Autowired
    IProjetService iProjetService;
    @Autowired
    IUserService iUserService;
    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un mangwa", tags = "Projet", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Projet.class)))),
            @ApiResponse(responseCode = "404", description = "Projet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> addProjet(@Valid @RequestBody ProjetReqDto projetReqDto) {
        Users u = userRepository.findById(projetReqDto.getUser()).get();
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(projetReqDto.getTransaction() != null ? EStatusTransaction.valueOf(projetReqDto.getTransaction()) : EStatusTransaction.DEPOT).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));
        Projet projet = new Projet();
        projet.setDate(projetReqDto.getDate());
//        projet.setMotif("Cotisation fond projet"+projetReqDto.getMotif());
        projet.setMotif("Cotisation fond projet "+u.getLastName());
        projet.setMontant(projetReqDto.getMontant());
        projet.setTypeTransaction(typeTransaction);
        projet.setUser(u);
        projet.setCreatedAt(LocalDateTime.now());
        iProjetService.createProjet(projet);

        return ResponseEntity.ok(projet);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des mangwa", tags = "Projet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("")
    public ResponseEntity<?> getProjet(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                         @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                         @RequestParam(required = false, defaultValue = "id") String sort,
                                       @RequestParam(required = false, value = "name") String member,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false, value = "date") LocalDate date,
                                       @RequestParam(required = false, value = "type") String type,
                                         @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Projet> list = iProjetService.getAllProjet(member, date, type,Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Liste des mangwa", tags = "Projet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Projet not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("/solde")
    public ResponseEntity<?> soldeProjet() {
        double solde = iProjetService.soldeProjet();
        return ResponseEntity.ok(solde);
    }

    @Operation(summary = "création des informations pour un bénéficiaire", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Beneficiaire.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> updateProjet(@RequestBody ProjetReqDto projetReqDto, @PathVariable Long id) {
        Users u = userRepository.findById(projetReqDto.getUser()).get();
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(projetReqDto.getTransaction() != null ? EStatusTransaction.valueOf(projetReqDto.getTransaction()) : EStatusTransaction.DEPOT).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));
        Projet projet = iProjetService.getById(id);
//        projet.setDate(projetReqDto.getDate());
//        projet.setMotif("Cotisation fond projet"+projetReqDto.getMotif());
        projet.setMotif(projetReqDto.getMotif());
        projet.setMontant(projetReqDto.getMontant());
        projet.setTypeTransaction(typeTransaction);
        projet.setUser(u);
        projet.setUpdatedAt(LocalDateTime.now());
        iProjetService.createProjet(projet);
        return ResponseEntity.ok(projet);
    }


    @Operation(summary = "création des informations pour un bénéficiaire", tags = "Beneficiaire", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Beneficiaire.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> deleteProjet(@PathVariable Long id) {
        Projet projet = iProjetService.getById(id);
        iProjetService.deleteProjets(id);
        return ResponseEntity.ok(projet);
    }


    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des prets par seance", tags = "Projet", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/seance/{idSeance:[0-9]+}")
    public ResponseEntity<Page<Projet>> getProjetBySeance(@PathVariable Long idSeance,
                                                                      @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                      @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                      @RequestParam(required = false, defaultValue = "id") String sort,
                                                                      @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Projet> list = iProjetService.getProjetBySeance(idSeance, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
}
