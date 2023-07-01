package com.adeli.adelispringboot.Users.repository;


import com.adeli.adelispringboot.Users.entity.ERole;
import com.adeli.adelispringboot.Users.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleUserRepo extends JpaRepository<RoleUser, Long> {
	Optional<RoleUser> findByName(ERole name);
	boolean existsByName(ERole name);
}
