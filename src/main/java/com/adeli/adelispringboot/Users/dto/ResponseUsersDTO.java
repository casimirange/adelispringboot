package com.adeli.adelispringboot.Users.dto;

import com.adeli.adelispringboot.Users.entity.ERole;
import com.adeli.adelispringboot.Users.entity.RoleUser;
import com.adeli.adelispringboot.Users.entity.StatusUser;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class ResponseUsersDTO {

	private Long userId;

	private String email;

	private String telephone;

	private Set<RoleUser> roles;

	private StatusUser status;

	private TypeAccount typeAccount;

	private List<ERole> roleNames;

	private String otpCode;

	private String firstName;

	private Double montant;

	private String lastName;

	private LocalDateTime dateLastLogin;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

}
