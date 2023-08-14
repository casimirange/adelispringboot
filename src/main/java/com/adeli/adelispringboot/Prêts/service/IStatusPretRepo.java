package com.adeli.adelispringboot.Prêts.service;

import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Prêts.entity.EStatusPret;
import com.adeli.adelispringboot.Prêts.entity.StatutPret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStatusPretRepo extends JpaRepository<StatutPret,Long> {
    Optional<StatutPret> findByName(EStatusPret name);
    boolean existsByName(EStatusPret name);
}
