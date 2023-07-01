package com.adeli.adelispringboot.Users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserModifyReqDto {

	@NotNull(message = "{phone.required}")
	@Schema(description = "Téléphone")
	private String telephone;

	@NotNull(message = "{firstName.required}")
	@Schema(description = "Prénom")
	private String firstName;

	@Schema(description = "Nom")
	private String lastName;

	@Schema(description = "montant")
	private Double montant;

	@Schema(description = "Role de l'utilisateur, defaut ROLE_USER", defaultValue = "ROLE_USER", allowableValues = {"ROLE_SUPERADMIN", "ROLE_USER"})
	private String roleName;

	@Schema(description = "Type de compte de l'utilisateur, defaut ADHERANT", defaultValue = "ADHERANT", allowableValues = {"TRESORIER", "SECRETAIRE", "SENSCEUR", "PRESIDENT", "COMISSAIRE_AU_COMPTE", "PORTE_PAROLE", "ADHERANT"})
	private String typeAccount;

}
