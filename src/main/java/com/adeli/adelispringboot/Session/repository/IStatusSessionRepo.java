package com.adeli.adelispringboot.Session.repository;

import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStatusSessionRepo extends JpaRepository<SessionStatus,Long> {
    Optional<SessionStatus> findByName(EStatusSession name);
    boolean existsByName(EStatusSession name);
}
