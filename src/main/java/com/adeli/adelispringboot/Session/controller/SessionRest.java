package com.adeli.adelispringboot.Session.controller;


import com.adeli.adelispringboot.Session.dto.SessionResDto;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Session.service.ISessionService;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.service.IUserService;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
import com.adeli.adelispringboot.authentication.service.JwtUtils;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@Tag(name = "Session")
@RequestMapping("/api/v1.0/session")
@Slf4j
public class SessionRest {

    @Autowired
    ResourceBundleMessageSource messageSource;
    @Autowired
    IEmailService emailService;

    @Autowired
    ISessionService iSessionService;

    @Autowired
    ISessionRepo iSessionRepo;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;

    @Autowired
    ApplicationContext appContext;

    @Autowired
    IUserService iUserService;

    @Value("${mail.from[0]}")
    String mailFrom;
    @Value("${mail.replyTo[0]}")
    String mailReplyTo;

    SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dateYear = new SimpleDateFormat("yyyy");

    @Operation(summary = "création des informations pour une session", tags = "Session", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Session.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public ResponseEntity<?> addSession(@Valid @RequestBody SessionResDto sessionResDto) {

        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));

        if (iSessionRepo.existsSessionByStatus(sessionStatus)) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.session_exist", null, LocaleContextHolder.getLocale())));
        }
        Session s = iSessionService.findLastSession();
        if (s != null) {
            if (sessionResDto.getBeginDate().isBefore(s.getEndDate())) {
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.session_order_date", null, LocaleContextHolder.getLocale())));
            }
        }

        Session session = new Session();
        session.setCreatedAt(LocalDateTime.now());
        session.setBeginDate(sessionResDto.getBeginDate());
        session.setEndDate(sessionResDto.getEndDate());
        session.setMangwa(sessionResDto.getMangwa());
        session.setName("" + sessionResDto.getBeginDate().getYear());
        session.setTax(sessionResDto.getTax());
        session.setStatus(sessionStatus);
        iSessionService.createSession(session);

        List<Users> usersList = iUserService.getUsers();

        String emailToEnableUser = "";
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("beginDate", sessionResDto.getBeginDate());
        emailProps.put("endDate", sessionResDto.getEndDate());
        emailProps.put("tax", sessionResDto.getTax());
        emailProps.put("mangwa", sessionResDto.getMangwa());
        emailProps.put("creator", sessionResDto.getCreator());

        for (Users user : usersList) {
            if (user.getStatus().getName() == EStatusUser.USER_ENABLED) {
                emailToEnableUser = user.getEmail();
                emailProps.put("name", user.getLastName());
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToEnableUser, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_NEW_SESSION, ApplicationConstant.TEMPLATE_EMAIL_NEW_SESSION));
                log.info("Email send successfull to user: " + emailToEnableUser);
            }
        }

        return ResponseEntity.ok(session);
    }

    @Operation(summary = "modification des informations pour une session", tags = "Session", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Session.class)))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/{name}")
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public ResponseEntity<?> updateSession(@Valid @RequestBody SessionResDto sessionResDto, @PathVariable String name) {

        if (iSessionService.getSessionByName(name).get().getName().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.session_exists", null, LocaleContextHolder.getLocale())));
        }
        Session s = iSessionService.findLastSession();
        if (s != null) {
            if (sessionResDto.getBeginDate().isBefore(s.getEndDate())) {
                return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("messages.session_order_date", null, LocaleContextHolder.getLocale())));
            }
        }
        Session session = iSessionService.getSessionByName(name).get();
        session.setUpdatedAt(LocalDateTime.now());
        session.setBeginDate(sessionResDto.getBeginDate());
        session.setEndDate(sessionResDto.getEndDate());
        session.setMangwa(sessionResDto.getMangwa());
        session.setTax(sessionResDto.getTax());
        session.setName("" + sessionResDto.getBeginDate().getYear());

        iSessionService.createSession(session);

        return ResponseEntity.ok(session);
    }

    @Operation(summary = "Recupérer Une Session par son id", tags = "Session", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Session.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','USER')")
    @GetMapping("/{name}")
    public ResponseEntity<Session> getSessionByName(@PathVariable String name) {
        return ResponseEntity.ok(iSessionService.getSessionByName(name).get());
    }

    @Operation(summary = "filtrer les sessions par nom", tags = "Session", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Session.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    @GetMapping("/like/{name}")
    public ResponseEntity<?> getClientsByCompanyNameLike(@PathVariable String name) {
        List<Session> sessions = iSessionService.getSessionsByNameLike(name);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "Terminer une session", tags = "Session", responses = {
            @ApiResponse(responseCode = "200", description = "Client deleted successfully", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : access denied", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('USER') or hasRole('USER')")
    @DeleteMapping("/{name}")
    public ResponseEntity<Object> deleteSession(@PathVariable String name) throws JRException, IOException {
        Session session = iSessionService.getSessionByName(name).get();

        List<Users> usersList = iUserService.getUsers();

        String emailToEnableUser = "";
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("beginDate", session.getBeginDate().toString());

        byte[] data = generateBilan(session);
        for (Users user : usersList) {
            if (user.getStatus().getName() == EStatusUser.USER_ENABLED) {
                emailToEnableUser = user.getEmail();
                emailProps.put("name", user.getLastName());
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, emailToEnableUser, mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_END_SESSION, ApplicationConstant.TEMPLATE_EMAIL_END_SESSION, data));
                log.info("Email send successfull to user: " + emailToEnableUser);
            }
        }
        iSessionService.endSession(session);
        return ResponseEntity.ok(new MessageResponseDto(
                messageSource.getMessage("messages.request_successful-delete", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des sessions", tags = "Session", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getSessions(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                         @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                         @RequestParam(required = false, defaultValue = "id") String sort,
                                         @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Session> list = iSessionService.getAllSessions(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    private byte[] generateBilan(Session order) throws JRException, IOException {
        List<Session> products = iSessionRepo.findAll();

        SessionResDto productDTO = new SessionResDto();
        List<SessionResDto> productDTOList = new ArrayList<>();

        for(Session product : products){

            productDTO.setTax(product.getTax());
            productDTO.setMangwa(product.getMangwa());
            productDTO.setBeginDate(product.getBeginDate());
            productDTO.setEndDate(product.getEndDate());
            productDTOList.add(productDTO);
        }

        /* Map to hold Jasper report Parameters*/
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("products", productDTOList);
        parameters.put("type", "BILAN");
        parameters.put("tax", order.getTax()+"");
        parameters.put("dateOrder", dateFor.format(Date.from((order.getUpdatedAt() == null) ? order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant():  order.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant())).toString());
        parameters.put("the_date", dateFor.format(new Date()).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/logo.jpeg");
        //Compile to jasperReport
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
        /* read jrxl fille and creat jasperdesign object*/
        Resource resource = appContext.getResource("classpath:/templates/jasper/invoice.jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();

        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        /* compiling jrxml with help of JasperReport class*/
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        /* Using jasperReport objet to generate PDF*/
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        /*convert PDF to byte type*/
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
