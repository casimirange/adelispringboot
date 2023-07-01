package com.adeli.adelispringboot.Users.repository;

import com.adeli.adelispringboot.Users.entity.ETypeAccount;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITypeAccountRepository extends JpaRepository<TypeAccount,Long> {
    Optional<TypeAccount> findByName(ETypeAccount name);
    Optional<TypeAccount> findByName(String name);
    boolean existsByName(ETypeAccount name);
}
