package com.adeli.adelispringboot.Users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
@Table(name = "users")
public class Users {

    @Schema(description = "identifiant unique de l'utilisateur", example = "1", required = true, accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long userId;

    @Column(unique = true,nullable = false)
    @Email
    @Schema(description = "adresse mail de l'utilisateur", example = "example@example.com")
    private String email;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "montant de cotisation de l'utilisateur", example = "500 €")
    private Double montant;

    @Schema(description = "mot de passe de l'utilisateur", example = "ebkn46Ai?")
    @NotNull
    @JsonIgnore
    private String password;

    @Schema(description = "Téléphone de l'utilisateur", example = "690362808?")
    @Column(nullable = true, unique = true)
    private String telephone;

    @JsonIgnore
    private String tokenAuth;	// utilisé pour vérifier l'activation du compte d'une part et la non revocation du refresh tokenAuth d'autre part

    @JsonIgnore
    private String notificationKey;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleUser> roles;

    @ManyToOne
    private StatusUser status;

    @ManyToOne
    private TypeAccount typeAccount;

    @ManyToMany
    @JsonIgnore
    private List<OldPassword> oldPasswords = new ArrayList<>();

    @Transient
    @Schema(hidden = true)
    @JsonIgnore
    private List<ERole> roleNames;

    private String otpCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime otpCodeCreatedAT;

    private boolean emailverify;

    private boolean phoneverify;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateLastLogin;

    private boolean isFirstConnection;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private boolean isDelete = false;
    public Users(Long userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public Users(Long userId, String email, String telephone, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.telephone = telephone;
    }

    public Users(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

}
