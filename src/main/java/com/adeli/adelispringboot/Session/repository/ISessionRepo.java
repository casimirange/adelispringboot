package com.adeli.adelispringboot.Session.repository;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISessionRepo extends JpaRepository<Session, Long> {

    Optional<Session> getSessionByName(String name);
    List<Session> getSessionsByNameContains(String name);
    boolean existsSessionByStatus(SessionStatus sessionStatus);
    Session findSessionByStatus(SessionStatus sessionStatus);
    Session findFirstByOrderByIdDesc();
}
