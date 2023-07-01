package com.adeli.adelispringboot.Users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserReqDto {

	@Email(message = "{email.verified}")
	@NotNull(message = "email.required")
	@Schema(description = "adresse email")
	private String email;

	@NotNull(message = "{phone.required}")
	@Schema(description = "Téléphone")
	private String telephone;

	@NotNull(message = "{montant.required}")
	@Schema(description = "Montant")
	private Double montant;

	@Size(min = 6,message = "{password.lenght}")
	@NotNull(message = "{password.required}")
	@Pattern.List({
			@Pattern(regexp = "(?=.*[0-9]).+", message = "{password.number}")
			,
			@Pattern(regexp = "(?=.*[a-z]).+", message = "{password.lowercase}")
			,
			@Pattern(regexp = "(?=.*[A-Z]).+", message = "{password.upercase}")
			,
			@Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message = "{password.sepcialcaracter}")
			,
			@Pattern(regexp = "(?=\\S+$).+", message = "{password.spacer}")})
	@Schema(description = "Mot de passe")
	private String password;

	@NotNull(message = "{firstName.required}")
	@Schema(description = "Prénom")
	private String firstName;

	@NotNull(message = "lastName.required")
	@Schema(description = "Nom")
	private String lastName;

	@Schema(description = "Role de l'utilisateur, defaut ROLE_USER", defaultValue = "ROLE_USER", allowableValues = {"ROLE_ADMIN", "ROLE_SUPERADMIN", "ROLE_AGENT","ROLE_PRE_VERIFICATION_USER","ROLE_USER"})
	private String roleName;

	@Schema(description = "Type de compte de l'utilisateur, defaut MANAGER_STORE", defaultValue = "MANAGER_STORE", allowableValues = {"STORE_KEEPER", "MANAGER_COUPON", "MANAGER_STORE", "TREASURY", "CUSTOMER_SERVICE", "MANAGER_STATION", "POMPIST"})
	private String typeAccount;

}
