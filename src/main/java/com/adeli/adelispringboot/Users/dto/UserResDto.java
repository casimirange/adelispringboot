package com.adeli.adelispringboot.Users.dto;

import com.adeli.adelispringboot.Users.entity.RoleUser;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(value = Include.NON_NULL)
public class UserResDto{
	
	private Long userId;

	private Long internalReference;

	private String email;

	private String telephone;

	private String lastName;

	private String firstName;

	private Double montant;

	private List<RoleUser> roles;

	private TypeAccount typeAccount;


}
