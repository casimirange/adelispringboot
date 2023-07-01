package com.adeli.adelispringboot.Users.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UserEditDto {

	@Email
	private String email;
	
	private String telephone;

	private String firstname;

	private String lastname;

}
