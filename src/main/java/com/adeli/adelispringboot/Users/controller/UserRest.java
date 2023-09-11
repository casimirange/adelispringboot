package com.adeli.adelispringboot.Users.controller;

import com.adeli.adelispringboot.Users.dto.ResponseUsersDTO;
import com.adeli.adelispringboot.Users.dto.UserReqDto;
import com.adeli.adelispringboot.Users.dto.UserResDto;
import com.adeli.adelispringboot.Users.entity.*;
import com.adeli.adelispringboot.Users.repository.IRoleUserRepo;
import com.adeli.adelispringboot.Users.repository.ITypeAccountRepository;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import com.adeli.adelispringboot.Users.service.IUserService;
import com.adeli.adelispringboot.authentication.dto.MessageResponseDto;
import com.adeli.adelispringboot.configuration.globalConfiguration.ApplicationConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@Tag(name = "users")
@RequestMapping("/api/v1.0/users")
@Slf4j
public class UserRest {

    @Autowired
    private IUserRepo iUserRepo;
    @Autowired
    IRoleUserRepo roleRepo;
    @Autowired
    ITypeAccountRepository typeAccountRepo;
    @Autowired
    private IUserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Operation(summary = "Recupérer un utilisateur à partir de son id", tags = "users", responses = {
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans la BD", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @GetMapping("/{userId:[0-9]+}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    public ResponseEntity<?> getByIds(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @Operation(summary = "Liste de utilisateurs", tags = "users", responses = {
        @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
        @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json")) })
    @GetMapping("")
    @PreAuthorize("hasAnyRole('SUPERADMIN','USER')")
    public ResponseEntity<Page<ResponseUsersDTO>> get20Users(
            @RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
            @RequestParam(required = false, value = "size", defaultValue = "20") String sizeParam,
            @RequestParam(required = false, defaultValue = "userId") String sort,
            @RequestParam(required = false, defaultValue = "desc") String order) {
        Page<ResponseUsersDTO> users = userService.getUsers(Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "modifie le statut d'un compte ", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),})
    @PreAuthorize("hasRole('SUPERADMIN')")
    @PutMapping("/{id:[0-9]+}/status")
    public ResponseEntity<Users> editStatus(@PathVariable("id") Long userId, @RequestParam Long statusId) {
        Users user = userService.editStatus(userId, statusId);
        return ResponseEntity.ok(user);
    }


    @Operation(summary = "Modifier role d'un compte", tags = "users", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "404", description = "Erreur: Role not found", content = @Content(mediaType = "Application/Json")),})
     @PreAuthorize("hasRole('SUPERADMIN')")
    @PutMapping("/{userId:[0-9]+}/role")
    public ResponseEntity<Users> editProfil(@PathVariable Long userId, @RequestParam Long updateRoleId,
                                            @RequestParam Long addRoleId) {
        Users user = userService.getById(userId);
        Users user2 = userService.editProfil(user, updateRoleId, addRoleId);
        return ResponseEntity.ok(user2);
    }

    @Operation(summary = "modifie l'email d'un utilisateur ", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "Succès de l'opération", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),})
    @PreAuthorize("@authorizationService.canUpdateOwnerItem(#userId, 'User')")
    @PutMapping("/{userId:[0-9]+}/email")
    public ResponseEntity<?> editEmail(@PathVariable Long userId, @RequestParam String email) {
        if (userService.existsByEmail(email, userId)) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("messages.email_exists", null, LocaleContextHolder.getLocale())));
        }
        Users user = userService.editEmail(userId, email);
        UserResDto userResDto = modelMapper.map(user, UserResDto.class);
        return ResponseEntity.ok(userResDto);
    }

    @Operation(summary = "Supprimer un utilisateur", tags = "users", responses = {
            @ApiResponse(responseCode = "200", description = "utilisateur supprimé avec succès", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/{userId:[0-9]+}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId); // TODO Configure hibernate envers afin qu'il insère les données supprimées et
        // customiser revInfo afin qu'il insère l'auteur des actions
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Bloquer ou activer le compte d'un utilisateur", tags = "users", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(mediaType = "Application/Json")),})
    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/lockAndUnlockAccount/{id}/{status}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") Long userId, @PathVariable boolean status) {
        userService.lockAndUnlockUsers(userId, status);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto(HttpStatus.OK, messageSource
                .getMessage("messages.user_status_account_update", null, LocaleContextHolder.getLocale())));
    }

    @Parameters(value = {
            @Parameter(name = "sort", schema = @Schema(allowableValues = {"id", "createdAt"})),
            @Parameter(name = "order", schema = @Schema(allowableValues = {"asc", "desc"}))})
    @Operation(summary = "Filtre des utilisateurs", tags = "users", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "403", description = "Forbidden : accès refusé", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "404", description = "Coupon not found", content = @Content(mediaType = "Application/Json")),
            @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = "Application/Json"))})
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN','AGENT','USER')")
    @GetMapping("/filter")
    public ResponseEntity<?> filtrerUsers(@RequestParam(required = false, value = "page", defaultValue = "0") String pageParam,
                                          @RequestParam(required = false, value = "firstName") String firstName,
                                          @RequestParam(required = false, value = "lastName") String lastName,
                                          @RequestParam(required = false, value = "typeAccount") String type,
                                          @RequestParam(required = false, value = "montant") Double montant,
                                          @RequestParam(required = false, value = "status") String status,
                                          @RequestParam(required = false, value = "size", defaultValue = ApplicationConstant.DEFAULT_SIZE_PAGINATION) String sizeParam,
                                          @RequestParam(required = false, defaultValue = "userId") String sort,
                                          @RequestParam(required = false, defaultValue = "desc") String order) throws JsonProcessingException {
        Page<ResponseUsersDTO> list = userService.filtres(status, type, montant, firstName, lastName, Integer.parseInt(pageParam), Integer.parseInt(sizeParam), sort, order);
        return ResponseEntity.ok(list);
    }

    @Parameters(value = {
            @Parameter(name = "typeAccount", schema = @Schema(allowableValues = {"STORE_KEEPER", "MANAGER_COUPON", "MANAGER_SPACES_2", "COMPTABLE", "DSI_AUDIT", "MANAGER_SPACES_1", "COMMERCIAL_ATTACHE", "SALES_MANAGER", "MANAGER_STORE", "MANAGER_ORDER", "TREASURY", "CUSTOMER_SERVICE", "MANAGER_STATION", "POMPIST"}))})
    @Operation(summary = "Inscription sur l'application", tags = "authentification", responses = {
            @ApiResponse(responseCode = "201", description = "Utilisateur crée avec succès", content = @Content(mediaType = "Application/Json", array = @ArraySchema(schema = @Schema(implementation = UserResDto.class)))),
            @ApiResponse(responseCode = "400", description = "Erreur: Ce nom d'utilisateur est déjà utilisé/Erreur: Cet email est déjà utilisé", content = @Content(mediaType = "Application/Json")),})
    @PutMapping("/update/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserReqDto userAddDto, @PathVariable String userId) throws JsonProcessingException {

        Users u = iUserRepo.findById(Long.parseLong( userId)).get();

        u.setEmail(userAddDto.getEmail());
        u.setTelephone(userAddDto.getTelephone());
        u.setFirstName(userAddDto.getFirstName());
        u.setLastName(userAddDto.getLastName());
        u.setUpdatedDate(LocalDateTime.now());
        Set<RoleUser> roles = new HashSet<>();
        RoleUser rolesUser = roleRepo.findByName(userAddDto.getRoleName() != null ? ERole.valueOf(userAddDto.getRoleName()) : ERole.ROLE_USER).orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        roles.add(rolesUser);
        u.setRoles(roles);
        TypeAccount typeAccount = typeAccountRepo.findByName(userAddDto.getTypeAccount() != null ? ETypeAccount.valueOf(userAddDto.getTypeAccount()) : ETypeAccount.ADHERANT).orElseThrow(()-> new ResourceNotFoundException("Type de compte not found"));
        u.setTypeAccount(typeAccount);
        u.setMontant(userAddDto.getMontant());
        iUserRepo.save(u);


        return ResponseEntity.status(HttpStatus.CREATED).body(u);
    }

}
