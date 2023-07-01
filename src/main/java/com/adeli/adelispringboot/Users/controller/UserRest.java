package com.adeli.adelispringboot.Users.controller;

import com.adeli.adelispringboot.Users.dto.ResponseUsersDTO;
import com.adeli.adelispringboot.Users.dto.UserResDto;
import com.adeli.adelispringboot.Users.entity.Users;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "users")
@RequestMapping("/api/v1.0/users")
@Slf4j
public class UserRest {
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

}
