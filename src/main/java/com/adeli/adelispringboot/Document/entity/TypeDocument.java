package com.adeli.adelispringboot.Document.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class TypeDocument {
	
	@Schema(description = "identifiant unique du type", example = "1", required = true, accessMode = AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long Id;

	@Schema(description = "nom du TYPE", example = "INVOICE")
	@Enumerated(EnumType.STRING)
	private ETypeDocument name;

}
