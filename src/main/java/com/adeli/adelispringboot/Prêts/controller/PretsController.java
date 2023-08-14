/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Prêts.controller;

import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IRetenueRepository;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Prêts.dto.PretReqDto;
import com.adeli.adelispringboot.Prêts.entity.EStatusPret;
import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Prêts.entity.StatutPret;
import com.adeli.adelispringboot.Prêts.repository.PretRepository;
import com.adeli.adelispringboot.Prêts.service.IPretService;
import com.adeli.adelispringboot.Prêts.service.IStatusPretRepo;
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
import java.util.List;


/**
 *
 * @author Casimir
 */

@RestController
@Tag(name = "Prêt")
@RequestMapping("/api/v1.0/pret")
@Slf4j
public class PretsController {

    @Autowired
    private ResourceBundleMessageSource messageSource;
//    @Autowired
//    TontineRepository tontineRepository;
    
    @Autowired
    ISessionRepo sessionRepository;

    @Autowired
    IPretService iPretService;

    @Autowired
    ISeanceService iSeanceService;

    @Autowired
    ITontineService iTontineService;
    
    @Autowired
    IUserRepo userRepository;

    @Autowired
    IUserService iUserService;
    
    @Autowired
    IRetenueRepository IRetenueRepository;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;

    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    @Autowired
    IStatusPretRepo iStatusPretRepo;
    
    @Autowired
    PretRepository pretRepository;
    String mts;

    @Operation(summary = "création des informations pour un prêt", tags = "Prêt", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Prets.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> createPret(@RequestBody PretReqDto pretReqDto) {
        Prets prets = new Prets();
        Tontine tontine = new Tontine();
        Users user = iUserService.getById(pretReqDto.getIdUser());
        Seance seance = iSeanceService.getById(pretReqDto.getIdSeance());
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(EStatusTransaction.PRET).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));
        StatutPret statutPret = iStatusPretRepo.findByName(EStatusPret.EN_COURS).orElseThrow(()-> new ResourceNotFoundException("Statut prêt not found"));

        if(iTontineService.soldeTontine() < pretReqDto.getMontant_prete()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.montant_insuffisant", null, LocaleContextHolder.getLocale())));
        }
        prets.setSeance(seance);
        prets.setUser(user);
        prets.setCreatedAt(LocalDateTime.now());
        prets.setMontant_prete(pretReqDto.getMontant_prete());
        prets.setDateRemboursement(null);
        prets.setMontant_rembourse(0);
        prets.setTypeTransaction(typeTransaction);
        prets.setStatutPret(statutPret);

        tontine.setTypeTransaction(typeTransaction);
        tontine.setMontant(pretReqDto.getMontant_prete());
        tontine.setSeance(seance);
        tontine.setDescription("prêt de "+user.getLastName());
        tontine.setUser(user);
        tontine.setCreatedAt(LocalDateTime.now());

        iPretService.createPret(prets);
        iTontineService.createTontine(tontine);
        return ResponseEntity.ok(prets);
    }

    @Operation(summary = "remboursement pour un prêt", tags = "Prêt", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Prets.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @PutMapping("/rembourser/{idPret:[0-9]+}")
    public ResponseEntity<?> remboursePret(@RequestBody PretReqDto pretReqDto, @PathVariable Long idPret) {
        Prets prets = iPretService.getById(idPret);
        Tontine tontine = new Tontine();
        Users user = iUserService.getById(pretReqDto.getIdUser());
        Seance seance = iSeanceService.getByDate(pretReqDto.getDateRemboursement());
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(EStatusTransaction.REMBOURSEMENT).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));
        StatutPret statutPret = iStatusPretRepo.findByName(pretReqDto.isType() ? EStatusPret.INNACHEVE : EStatusPret.ACHEVE).orElseThrow(()-> new ResourceNotFoundException("Statut prêt not found"));
        System.out.println("montant prété "+prets.getMontant_prete());
        if(pretReqDto.getMontant_rembourse() < prets.getMontant_prete()){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.pret_rembourse_insuffisant", null, LocaleContextHolder.getLocale())));
        }
        if(!prets.getTypeTransaction().getName().equals(EStatusTransaction.PRET)){
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.pret_rembourse", null, LocaleContextHolder.getLocale())));
        }
//        prets.setSeance(seance);
//        prets.setUser(user);
        prets.setUpdatedAt(LocalDateTime.now());
        prets.setMontant_rembourse(pretReqDto.getMontant_rembourse());
        prets.setDateRemboursement(seance.getDate());
//        prets.setMontant_prete(prets.getMontant_prete());
        prets.setTypeTransaction(typeTransaction);
        prets.setStatutPret(statutPret);

        tontine.setTypeTransaction(typeTransaction);
        tontine.setMontant(pretReqDto.getMontant_rembourse());
        tontine.setSeance(seance);
        tontine.setDescription("remboursement prêt du "+prets.getSeance().getDate());
        tontine.setUser(user);
        tontine.setCreatedAt(LocalDateTime.now());

        iPretService.createPret(prets);
        iTontineService.createTontine(tontine);
        return ResponseEntity.ok(prets);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des prêts", tags = "Prêt", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("")
    public Page<Prets> getPrets(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                @RequestParam(required = false, defaultValue = "idPret") String sort,
                                @RequestParam(required = false, defaultValue = "desc") String order) {
        return iPretService.getAllPret(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des prets par seance", tags = "Prêt", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/seance/{idSeance:[0-9]+}")
    public ResponseEntity<Page<Prets>> getPretBySeance(@PathVariable Long idSeance,
                                                                  @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                                                  @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                                                  @RequestParam(required = false, defaultValue = "idPret") String sort,
                                                                  @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Prets> list = iPretService.getPretBySeance(idSeance, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

//    @GetMapping("/solde")
//    public JSONObject soldeTontine(){
//        Tontine tontine = tontineRepository.findFirstByOrderByIdTontineDesc();
//        Map<String, Object> response = new HashMap<>();
//        JSONObject solde;
//        response.put("solde", tontine.getMontant());
//        solde = new JSONObject(response);
//        return solde;
//    }

    @DeleteMapping("/{id}")
    public void deletePret(@PathVariable Long id) {
            pretRepository.delete(pretRepository.findById(id).get());
    }
           
    @GetMapping("/id/{id}")
    public List<JSONObject> getUsers(@PathVariable Long id) {
        Users u = userRepository.findById(id).get();
        
        return pretRepository.findPretsUser(id);
    }
        
}
