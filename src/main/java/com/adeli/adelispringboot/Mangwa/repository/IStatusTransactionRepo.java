package com.adeli.adelispringboot.Mangwa.repository;

import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStatusTransactionRepo extends JpaRepository<TypeTransaction,Long> {
    Optional<TypeTransaction> findByName(EStatusTransaction name);
    boolean existsByName(EStatusTransaction name);
}
