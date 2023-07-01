package com.adeli.adelispringboot.Users.repository;

import com.adeli.adelispringboot.Users.entity.OldPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOldPasswordRepo extends JpaRepository<OldPassword, Long> {

}
