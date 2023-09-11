/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Amandes.controller;

import com.adeli.adelispringboot.Amandes.dto.AmandeReqDto;
import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Amandes.repository.IAmandeRepo;
import com.adeli.adelispringboot.Amandes.service.IAmandeService;
import com.adeli.adelispringboot.Discipline.entity.ETypeDiscipline;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Mangwa.service.IMangwaService;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.service.ISessionService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
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
import org.springframework.format.annotation.DateTimeFormat;
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
 * @author Casimir
 */
@RestController
@Tag(name = "Amande")
@RequestMapping("/api/v1.0/amandes")
@Slf4j
public class AmandesController {
    @Autowired
    ISessionRepo sessionRepository;

    @Autowired
    IUserRepo userRepository;

    @Autowired
    IUserService iUserService;

    @Autowired
    IAmandeRepo amandeRepository;
    @Autowired
    IAmandeService iAmandeService;

    @Autowired
    ISeanceService iSeanceService;

    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Autowired
    IMangwaService iMangwaService;

    @Autowired
    ISessionService iSessionService;

    EStatusTransaction etyp = null;

    @Operation(summary = "création des informations pour une amande", tags = "Amande", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Amande.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> createAmande(@RequestBody AmandeReqDto amandeReqDto) {
        Users user = iUserService.getById(amandeReqDto.getIdUser());
        Amande amande = new Amande();

        Seance seance = iSeanceService.getById(amandeReqDto.getIdSeance());
        amande.setMontant(amandeReqDto.getMontant());
        amande.setMotif(amandeReqDto.getMotif());
        amande.setSeance(seance);
        amande.setDate(seance.getDate());
        amande.setCreatedAt(LocalDateTime.now());
        amande.setUser(user);
        if (amandeReqDto.pay) {
            etyp = EStatusTransaction.DEPOT;
            Retenue retenue = new Retenue();
            Session session = iSessionService.findLastSession();
            retenue.setMontant(amandeReqDto.getMontant());
            TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(EStatusTransaction.DEPOT).orElseThrow(() -> new ResourceNotFoundException("Type de transaction not found"));
            retenue.setTypeTransaction(typeTransaction);
            retenue.setDate(seance.getDate());
            retenue.setMotif("amande " + user.getLastName());
            retenue.setCreatedAt(LocalDateTime.now());
            retenue.setUser(user);
            iMangwaService.createMangwa(retenue);
        } else {
            etyp = EStatusTransaction.NON_PAYE;
        }
        TypeTransaction typeTransaction2 = iStatusTransactionRepo.findByName(etyp).orElseThrow(() -> new ResourceNotFoundException("Type de transaction not found"));
        amande.setTypeTransaction(typeTransaction2);
        iAmandeService.createAmande(amande);
        return ResponseEntity.ok(amande);
    }

    @Operation(summary = "création des informations pour une amande", tags = "Amande", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Amande.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> updateAmande(@RequestBody AmandeReqDto amandeReqDto, @PathVariable Long id) {
        Users user = iUserService.getById(amandeReqDto.getIdUser());
        Amande amande = iAmandeService.getAmandeById(id);


        if (amandeReqDto.pay) {
            etyp = EStatusTransaction.DEPOT;
            Retenue retenue = new Retenue();
            Session session = iSessionService.findLastSession();
            retenue.setMontant(amandeReqDto.getMontant());
            TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(EStatusTransaction.DEPOT).orElseThrow(() -> new ResourceNotFoundException("Type de transaction not found"));
            retenue.setTypeTransaction(typeTransaction);
            retenue.setDate(amande.getDate());
            retenue.setMotif("amande " + user.getLastName());
            retenue.setCreatedAt(LocalDateTime.now());
            retenue.setUser(user);
            iMangwaService.createMangwa(retenue);
        } else {
            etyp = EStatusTransaction.NON_PAYE;
            if (!amande.getTypeTransaction().getName().equals(etyp)) {
                Retenue retenue = iMangwaService.getByMotifAndDateType("amande " + amande.getUser().getLastName(), amande.getMontant(), amande.getDate(), amande.getTypeTransaction());
                iMangwaService.deleteMangwas(retenue.getId());
            }
        }
        TypeTransaction typeTransaction2 = iStatusTransactionRepo.findByName(etyp).orElseThrow(() -> new ResourceNotFoundException("Type de transaction not found"));
        amande.setTypeTransaction(typeTransaction2);
        amande.setMontant(amandeReqDto.getMontant());
        amande.setMotif(amandeReqDto.getMotif());
        amande.setUpdatedAt(LocalDateTime.now());
        amande.setUser(user);
        iAmandeService.createAmande(amande);
        return ResponseEntity.ok(amande);
    }

    @Operation(summary = "création des informations pour une amande", tags = "Amande", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Amande.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> deleteAmande(@PathVariable Long id) {
        Amande amande = iAmandeService.getAmandeById(id);
        etyp = EStatusTransaction.DEPOT;
        if (amande.getTypeTransaction().getName().equals(etyp)) {
            Retenue retenue = iMangwaService.getByMotifAndDateType("amande " + amande.getUser().getLastName(), amande.getMontant(), amande.getDate(), amande.getTypeTransaction());
            iMangwaService.deleteMangwas(retenue.getId());
        }
        iAmandeService.deleteAmande(amande.getIdAmande());
        return ResponseEntity.ok(amande);
    }


//    @PostMapping("/retrait/{id}")
//    @Operation(summary = "retirer les sous dans la caisse amande", tags = "Amande", responses = {
//            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Amande.class)))),
//            @ApiResponse(responseCode = "404", description = "Amande not found", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
//    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
//    public ResponseEntity<?> retraitAmande(@PathVariable Long id, @RequestBody Amande amande) {
//        Users user = userRepository.findById(id).get();
//        Amande am = amandeRepository.findFirstByOrderByIdAmandeDesc();
//        double solde = 0;
//        if(am != null){
//            if(amande.getCredit() > am.getSolde()){
//                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
//                        messageSource.getMessage("messages.amount_exced", null, LocaleContextHolder.getLocale())));
//            }
//            solde = am.getSolde() - amande.getCredit();
//            amande.setSolde(solde);
//        }else{
//            solde = amande.getCredit();
//            amande.setSolde(solde);
//        }
//
//        Session session = sessionRepository.findFirstByOrderByIdDesc();
//        amande.setDate(LocalDate.now());
//        amande.setSession(session);
//        amande.setDebit(0);
//        amande.setUser(user);
//        return ResponseEntity.ok(amandeRepository.saveAndFlush(amande));
//    }

//    @GetMapping()
//    @Operation(summary = "Liste des amandes", tags = "Amande", responses = {
//            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Amande.class)))),
//            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
//            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
//    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
//    public ResponseEntity<?> getAmandes(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
//                                        @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
//                                        @RequestParam(required = false, defaultValue = "id") String sort,
//                                        @RequestParam(required = false, defaultValue = "desc") String order){
//        Page<Amande> list = iAmandeService.getAllAmandes(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
//        return ResponseEntity.ok(list);
//    }


//    @GetMapping("/solde")
//    public JSONObject getSoldeAmande(){
//        Amande amande = amandeRepository.findFirstByOrderByIdAmandeDesc();
//        Map<String, Object> response = new HashMap<>();
//        JSONObject solde;
//        if(amande != null){
//            response.put("solde", amande.getSolde());
//        }else{
//            response.put("solde", 0);
//        }
//        solde = new JSONObject(response);
//        return solde;
//    }


    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des amandes par seance", tags = "Amande", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/seance/{idSeance:[0-9]+}")
    public ResponseEntity<Page<Amande>> getAmandeBySeance(@PathVariable Long idSeance,
                                                          @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                          @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                          @RequestParam(required = false, defaultValue = "idAmande") String sort,
                                                          @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Amande> list = iAmandeService.getAmandesBySeance(idSeance, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste de toutes les amandes", tags = "Amande", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping()
    public ResponseEntity<Page<Amande>> getAmandes(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                   @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                   @RequestParam(required = false, defaultValue = "idAmande") String sort,
                                                   @RequestParam(required = false, value = "name") String member,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false, value = "date") LocalDate date,
                                                   @RequestParam(required = false, value = "type") String type,
                                                   @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Amande> list = iAmandeService.getAllAmandes(member, date, type, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }
}
