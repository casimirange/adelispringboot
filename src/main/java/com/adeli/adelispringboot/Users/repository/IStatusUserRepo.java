package com.adeli.adelispringboot.Users.repository;


import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.StatusUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "statusUsers")
public interface IStatusUserRepo extends JpaRepository<StatusUser, Long> {
	StatusUser findByName(EStatusUser name);
	boolean existsByName(EStatusUser name);

}
