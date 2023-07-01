package com.adeli.adelispringboot.Users.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class RoleUser {
	@Schema(description = "identifiant unique du role", example = "1", required = true, accessMode = AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long Id;

	@Schema(description = "nom du role", example = "ROLE_ADMIN")
	@Enumerated(EnumType.STRING)
	private ERole name;

	public RoleUser(ERole name) {
		this.name = name;
	}
}
