package com.adeli.adelispringboot.Users.repository;

import com.adeli.adelispringboot.Users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IUserBaseRepo<T extends Users> extends JpaRepository<T, Long> {
	
}
