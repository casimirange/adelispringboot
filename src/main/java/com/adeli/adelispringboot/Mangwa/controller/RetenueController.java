/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Mangwa.controller;

import com.adeli.adelispringboot.Mangwa.dto.RetenueReqDto;
import com.adeli.adelispringboot.Mangwa.dto.RetenueResDto;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IRetenueRepository;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Mangwa.service.IMangwaService;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.ETypeAccount;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.Users.service.IUserService;
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
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
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
@Tag(name = "Mangwa")
@RequestMapping("/api/v1.0/mangwa")
@Slf4j
public class RetenueController {
    @Autowired
    ResourceBundleMessageSource messageSource;
    @Autowired
    IEmailService emailService;
    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    @Autowired
    IUserRepo userRepository;
    
    @Autowired
    IRetenueRepository IRetenueRepository;
    @Autowired
    IMangwaService iMangwaService;
    @Autowired
    IUserService iUserService;
    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    @Operation(summary = "création des informations pour un mangwa", tags = "Mangwa", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Retenue.class)))),
            @ApiResponse(responseCode = "404", description = "Mangwa not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    public ResponseEntity<?> addMangwa(@Valid @RequestBody RetenueReqDto retenueReqDto) {
        Users u = userRepository.findById(retenueReqDto.getUser()).get();
        TypeTransaction typeTransaction = iStatusTransactionRepo.findByName(retenueReqDto.getTransaction() != null ? EStatusTransaction.valueOf(retenueReqDto.getTransaction()) : EStatusTransaction.DEPOT).orElseThrow(()-> new ResourceNotFoundException("Type de transaction not found"));
        Retenue retenue = new Retenue();
        retenue.setDate(retenueReqDto.getDate());
        retenue.setMotif(retenueReqDto.getMotif());
        retenue.setMontant(retenueReqDto.getMontant());
        retenue.setTypeTransaction(typeTransaction);
        retenue.setUser(u);
        retenue.setCreatedAt(LocalDateTime.now());
        iMangwaService.createMangwa(retenue);

        if (typeTransaction.getName().equals(EStatusTransaction.RETRAIT)){
            double solde = 0;
            List<Users> usersList = iUserService.getUsers();

            String emailToEnableUser = "";
            Map<String, Object> emailProps = new HashMap<>();
            emailProps.put("date", retenueReqDto.getDate());
            emailProps.put("montant", retenueReqDto.getMontant());
            emailProps.put("motif", retenueReqDto.getMotif());
            emailProps.put("solde", solde);

            for (Users user : usersList) {
                if (user.getStatus().getName() == EStatusUser.USER_ENABLED) {
                    emailToEnableUser = user.getEmail();
                    emailProps.put("name", user.getLastName());
                    emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToEnableUser, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MANGWA, ApplicationConstant.TEMPLATE_EMAIL_MANGWA));
                    log.info("Email send successfully to user: " + emailToEnableUser);
                }
            }
        }

        return ResponseEntity.ok(retenue);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des mangwa", tags = "Mangwa", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("")
    public ResponseEntity<?> getMangwa(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                         @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                         @RequestParam(required = false, defaultValue = "id") String sort,
                                       @RequestParam(required = false, value = "name") String member,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false, value = "date") LocalDate date,
                                       @RequestParam(required = false, value = "type") String type,
                                         @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Retenue> list = iMangwaService.getAllMangwa(member, date, type,Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Liste des mangwa", tags = "Mangwa", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Mangwa not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'USER')")
    @GetMapping("/solde")
    public ResponseEntity<?> soldeMangwa() {
        double solde = iMangwaService.soldeMangwa();
        return ResponseEntity.ok(solde);
    }

//    @PostMapping("")
//    public ResponseEntity<ProjetResDto> createMangwa(@RequestBody ProjetReqDto retenueReqDto) {
//        Users user = userRepository.findById(u.getUserId()).get();
//        Projet retenue = IProjetRepository.findFirstByOrderByIdRetenueDesc();
//        if(retenue  != null){
//            if(ret.getTransaction().equals("Débit")){
//                double solde = retenue.getSolde() + ret.getDebit();
//                ret.setSolde(solde);
//            }else {
//                double soldes = retenue.getSolde() - ret.getCredit();
//                ret.setSolde(soldes);
//            }
//        }else{
//            if(ret.getTransaction().equals("Débit")){
//                double solde = ret.getDebit();
//                ret.setSolde(solde);
//            }else {
//                double soldes = ret.getCredit();
//                ret.setSolde(soldes);
//            }
//        }
//
//        ret.setUser(user);
//
//        double x = ret.getDebit() + ret.getCredit();
//        IProjetRepository.save(ret);
//      return ResponseEntity.ok(ret);
//    }
    
//    @GetMapping("/solde")
//    public JSONObject soldeRetenue(){
//        Projet retenue = IProjetRepository.findFirstByOrderByIdRetenueDesc();
//        Map<String, Object> response = new HashMap<>();
//        JSONObject solde;
//        if(retenue != null){
//            response.put("solde", retenue.getSolde());
//        }else{
//            response.put("solde", 0);
//        }
//        solde = new JSONObject(response);
//        return solde;
//    }
}
