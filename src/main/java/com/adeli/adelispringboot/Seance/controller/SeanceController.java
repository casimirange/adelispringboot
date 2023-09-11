/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.Seance.controller;


//import com.example.demo.entity.Role;

import com.adeli.adelispringboot.Amandes.dto.AmandeReqDto;
import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Amandes.service.IAmandeService;
import com.adeli.adelispringboot.Beneficiaires.dto.BenefReqDto;
import com.adeli.adelispringboot.Beneficiaires.entity.Beneficiaire;
import com.adeli.adelispringboot.Beneficiaires.service.IBeneficiaireService;
import com.adeli.adelispringboot.Discipline.dto.DisciplineResDto;
import com.adeli.adelispringboot.Discipline.entity.Discipline;
import com.adeli.adelispringboot.Discipline.service.IDisciplineService;
import com.adeli.adelispringboot.Document.entity.ETypeDocument;
import com.adeli.adelispringboot.Document.entity.TypeDocument;
import com.adeli.adelispringboot.Document.repository.ITypeDocumentRepo;
import com.adeli.adelispringboot.Document.service.IDocumentStorageService;
import com.adeli.adelispringboot.Mangwa.dto.RetenueResDto;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.service.IMangwaService;
import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Prêts.service.IPretService;
import com.adeli.adelispringboot.Seance.dto.SeanceReqDto;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.repository.ISeanceRepository;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.service.ITontineService;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.Users.service.IUserService;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
import com.adeli.adelispringboot.configuration.email.dto.EmailDto;
import com.adeli.adelispringboot.configuration.email.service.IEmailService;
import com.adeli.adelispringboot.configuration.globalConfiguration.ApplicationConstant;
import com.ibm.icu.text.RuleBasedNumberFormat;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


/**
 *
 * @author Casimir
 */
@RestController
@Tag(name = "Seance")
@RequestMapping("/api/v1.0/seance")
@Slf4j
public class SeanceController {
    @Autowired
    private ISeanceRepository iSeanceRepository;
    @Autowired
    ResourceBundleMessageSource messageSource;
    @Autowired
    IEmailService emailService;

    @Autowired
    ISeanceService iSeanceService;

    @Autowired
    ITontineService iTontineService;

    @Autowired
    IPretService iPretService;

    @Autowired
    IMangwaService iMangwaService;

    @Autowired
    IDisciplineService iDisciplineService;

    @Autowired
    IAmandeService iAmandeService;

    @Autowired
    IBeneficiaireService iBeneficiaireService;

    @Autowired
    ISessionRepo iSessionRepo;

    @Autowired
    IUserRepo iUserRepo;

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

    @Autowired
    ITypeDocumentRepo iTypeDocumentRepo;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    IDocumentStorageService iDocumentStorageService;

    @Value("${app.api.base.url}")
    private String api_base_url;


    @Operation(summary = "création des informations pour une seance", tags = "Seance", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Seance.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<?> addSeance(@Valid @RequestBody SeanceReqDto seanceReqDto) {

        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.CREEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.CREEE + " n'existe pas"));
        Session session = iSessionRepo.findSessionByStatus(sessionStatus);

        if (iSeanceRepository.existsSeanceByStatus(sessionStatus)) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.seance_exist", null, LocaleContextHolder.getLocale())));
        }

        Users users = iUserService.getById(seanceReqDto.getUsers());
        Seance seance = new Seance();
        seance.setCreatedAt(LocalDateTime.now());
        seance.setDate(seanceReqDto.getDate());
        seance.setUsers(users);
        seance.setStatus(sessionStatus);
        seance.setSession(session);
        iSeanceService.createSeance(seance);
        log.info("Une nouvelle seance a été créée: " + seance.getId());

        return ResponseEntity.ok(seance);
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Liste des seances", tags = "Seance", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("")
    public ResponseEntity<?> getSeances(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                         @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                         @RequestParam(required = false, defaultValue = "id") String sort,
                                         @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<Seance> list = iSeanceService.getAllSeances(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Recupérer une séance à partir de son id", tags = "Seance", responses = {
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/{id:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    public ResponseEntity<?> getByIds(@PathVariable Long id) {
        return ResponseEntity.ok(iSeanceService.getById(id));
    }

    @Operation(summary = "Compte rendu de la séance", tags = "Seance", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Seance.class)))),
            @ApiResponse(responseCode = "404", description = "Seance not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PostMapping("/compterendu/{id:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','USER')")
    public ResponseEntity<?> compteRendu(@RequestBody MultipartFile file, @PathVariable Long id) {

        Seance seance = iSeanceService.getById(id);

//        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.INVOICE).orElseThrow(()-> new ResourceNotFoundException("Statut :  "  +  ETypeDocument.INVOICE +  "  not found"));
        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.DELIVERY).orElseThrow(()-> new ResourceNotFoundException("Statut :  "  +  ETypeDocument.DELIVERY +  "  not found"));
        iDocumentStorageService.storeFile(file, id, "pdf", typeDocument);
        String fileDownloadUri = api_base_url+"/api/v1.0/seance/file/" + id + "/downloadFile?type=delivery&docType=pdf";
        seance.setLinkCompteRendu(fileDownloadUri);
        seance.setUpdatedAt(LocalDateTime.now());
        iSeanceService.createSeance(seance);

        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("date", seance.getDate());

        List<Users> usersList = iUserService.getUsers();
        for (Users user : usersList) {
            if (user.getStatus().getName() == EStatusUser.USER_ENABLED) {
                emailProps.put("name", user.getLastName());
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_COMPTE_RENDU+seance.getDate(), ApplicationConstant.TEMPLATE_EMAIL_COMPTE_RENDU));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=compte rendu-" + seance.getDate() + ".pdf");

//        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        return ResponseEntity.ok(seance);
    }

    @Operation(summary = "Télécharger compte rendu pour une séance", tags = "Seance", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = "Application/Json")),})
    @GetMapping("/file/{id:[0-9]+}/downloadFile")
    public ResponseEntity<Object> downloadFile(@PathVariable("id") Long InternalReference, @Schema(required = true, allowableValues = {"INVOICE", "DELIVERY"}, description = "Type de document") @RequestParam("type") String type, @RequestParam("docType") String docType,
                                               HttpServletRequest request) {

        TypeDocument typeDocument = iTypeDocumentRepo.findByName(ETypeDocument.valueOf(type.toUpperCase())).orElseThrow(()-> new ResourceNotFoundException("Type de document:  "  +  type +  "  not found"));

        String fileName = iDocumentStorageService.getDocumentName(InternalReference, docType, typeDocument.getId());
        Resource resource = null;
        if (fileName != null && !fileName.isEmpty()) {
            try {
                resource = iDocumentStorageService.loadFileAsResource(fileName);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException e) {
                log.info("Could not determine file type.");
            }
            // Fallback to the default content type if type could not be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
    }

    @Operation(summary = "clôturer une seance", tags = "Seance", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = Seance.class)))),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/end/{id:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    public ResponseEntity<?> terminerSeance(@PathVariable() Long id) throws JRException, IOException {

        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.TERMINEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.TERMINEE + " n'existe pas"));

        Seance seance = iSeanceService.getById(id);
        log.info(seance.getDate()+"");
        seance.setUpdatedAt(LocalDateTime.now());
        seance.setStatus(sessionStatus);
        double sm = iMangwaService.soldeMangwa();
        double st = iTontineService.soldeTontine();
        double sc = sm + st;
        Page<Beneficiaire> beneficiaires = iBeneficiaireService.getBeneficiaireBySeance(id, 0, 20, "id", "desc");
//        Page<Prets> prets = iPretService.getAllPret("", null, "PRET", 0, 20, "idPret", "desc");
        Page<TontineResDto> tontines = iTontineService.getTontinesBySeance(seance.getId(), 0, 20, "idTontine", "desc");
        Page<Prets> prets = iPretService.getPretBySeance(seance.getId(), 0, 20, "idPret", "desc");
        Page<Amande> amandes = iAmandeService.getAmandesBySeance(seance.getId(), 0, 20, "idAmande", "desc");
        Page<Discipline> disciplines = iDisciplineService.getDisciplinesBySeance(seance.getId(), 0, 20, "id", "desc");
        Page<Retenue> mangwa = iMangwaService.getMangwaBySeance(seance.getId(), 0, 20, "id", "desc");
//        seance.getTontines().get(0).getTypeTransaction().getName();

//        log.info("La séance du " + seance.getDate() + " est terminée");
//        log.info("La séance du " + sm + " est terminée");
//        log.info("La séance du " + st + " est terminée");
//        log.info("La séance du " + sc + " est terminée");
//        log.info("La séance du " + prets.getContent().get(0).getMontant_prete() + " est terminée");
//        log.info("La séance du " + disciplines.getContent().get(0).getTypeDiscipline().getName() + " est terminée");

        
        String status  = seance.getStatus().getName().toString();
        String sm1  = sm+"";
//        byte[] data = generateReportSeance(seance);
        Map<String, Object> emailProps = new HashMap<>();
        emailProps.put("status", status);
        emailProps.put("date", seance.getDate());
        emailProps.put("soldeMangwa", sm1);
        emailProps.put("soldeTontine", st);
        emailProps.put("soldeCaisse", sc);
        emailProps.put("beneficiaires", beneficiaires);
        emailProps.put("prets", prets);
        emailProps.put("amandes", amandes);
        emailProps.put("disciplines", disciplines);
        emailProps.put("mangwa", mangwa);
        emailProps.put("tontines", tontines);
        List<Users> usersList = iUserService.getUsers();
        for (Users user : usersList) {
            if (user.getStatus().getName() == EStatusUser.USER_ENABLED) {
                emailProps.put("name", user.getLastName() +" "+user.getFirstName());
//                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_SEANCE+seance.getDate()+" - "+EStatusSession.TERMINEE, ApplicationConstant.TEMPLATE_EMAIL_END_SEANCE, data, "seance du " + seance.getDate() + ".pdf"));
                emailService.sendEmail(new EmailDto(mailFrom, ApplicationConstant.ENTREPRISE_NAME, user.getEmail(), mailReplyTo, emailProps, ApplicationConstant.SUBJECT_EMAIL_MODIFY_SEANCE+seance.getDate()+" - "+EStatusSession.TERMINEE, ApplicationConstant.TEMPLATE_EMAIL_END_SEANCE));
                log.info("Email  send successfull for user: " + user.getEmail());
            }
        }

//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=seance du " + seance.getDate() + ".pdf");
//        iSeanceService.createSeance(seance);
//        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        byte[] data = new byte[0];
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=seance du " + seance.getDate() + ".pdf");
        iSeanceService.createSeance(seance);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }


    private byte[] generateReportSeance(Seance seance) throws JRException, IOException {
//        List<Product> products = iProductService.getProductsByIdOrder(order.getInternalReference());
        Page<TontineResDto> tontines = iTontineService.getTontinesBySeance(seance.getId(), 0, 20, "idTontine", "desc");
        Page<Prets> prets = iPretService.getPretBySeance(seance.getId(), 0, 20, "idPret", "desc");
        Page<Amande> amandes = iAmandeService.getAmandesBySeance(seance.getId(), 0, 20, "idAmande", "desc");
        Page<Discipline> disciplines = iDisciplineService.getDisciplinesBySeance(seance.getId(), 0, 20, "id", "desc");
        Page<Retenue> mangwa = iMangwaService.getMangwaBySeance(seance.getId(), 0, 20, "id", "desc");
        Page<Beneficiaire> beneficiaires = iBeneficiaireService.getBeneficiaireBySeance(seance.getId(), 0, 20, "id", "desc");

        double soldeTontine = iTontineService.soldeTontine();
        double soldeMangwa = iMangwaService.soldeMangwa();
        double soldeCaisse = soldeTontine + soldeMangwa;
        RuleBasedNumberFormat ruleBasedNumberFormat = new RuleBasedNumberFormat(Locale.FRANCE, RuleBasedNumberFormat.SPELLOUT);
        String solde = ruleBasedNumberFormat.format(soldeCaisse);
        TontineResDto productDTO = new TontineResDto();
        RetenueResDto retenueResDto = new RetenueResDto();
        AmandeReqDto amandeReqDto = new AmandeReqDto();
        BenefReqDto benefReqDto = new BenefReqDto();
        DisciplineResDto disciplineResDto = new DisciplineResDto();
        List<TontineResDto> tontineDTOList = new ArrayList<>();
        List<RetenueResDto> retenueDTOList = new ArrayList<>();
        List<AmandeReqDto> amandeDtoList = new ArrayList<>();
        List<BenefReqDto> benefDtoLisy = new ArrayList<>();
        List<DisciplineResDto> disciplineDtoList = new ArrayList<>();


        for(TontineResDto product : tontines){
            productDTO = new TontineResDto();
            productDTO.setName(product.getUser().getFirstName());
            productDTO.setUser(product.getUser());
            productDTO.setTypeTransaction(product.getTypeTransaction());
            productDTO.setStatus(product.getTypeTransaction().getName().toString());
            productDTO.setMontant(product.getMontant());
            productDTO.setDescription(product.getDescription());
            tontineDTOList.add(productDTO);
        }

        for(Retenue retenueResDto1 : mangwa){

            retenueResDto.setName(retenueResDto1.getUser().getFirstName());
            retenueResDto.setUser(retenueResDto1.getUser());
            retenueResDto.setTransaction(retenueResDto1.getTypeTransaction().getName().toString());
            retenueResDto.setMontant(retenueResDto1.getMontant());
            retenueResDto.setMotif(retenueResDto1.getMotif());
            retenueDTOList.add(retenueResDto);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cotisations", tontineDTOList);
        parameters.put("prets", prets.getContent());
        parameters.put("amandes", amandes.getContent());
        parameters.put("mangwas", retenueDTOList);
        parameters.put("disciplines", disciplines.getContent());
        parameters.put("beneficiaire", beneficiaires.getContent());
        parameters.put("soldeMangwa", soldeMangwa+"");
        parameters.put("soldeTontine", soldeTontine+"");
        parameters.put("soldeCaisse", soldeCaisse+"");
        parameters.put("solde", solde);
        parameters.put("user", seance.getUsers().getLastName() + " " + seance.getUsers().getFirstName());
        parameters.put("session", "du "+ seance.getSession().getBeginDate() + " au "+ seance.getSession().getEndDate());
        parameters.put("status", seance.getStatus().getName().toString());
        parameters.put("dateSeance", seance.getDate().toString());
        parameters.put("the_date", dateFor.format(new Date()).toString());
        Resource resourceLogo = appContext.getResource("classpath:/templates/img/adeli.jpeg");
        //Compile to jasperReport
        InputStream inputStreamLogo = resourceLogo.getInputStream();
        parameters.put("logo", inputStreamLogo);
        /* read jrxl fille and creat jasperdesign object*/
        Resource resource = appContext.getResource("classpath:/templates/jasper/report.jrxml");
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
