package com.adeli.adelispringboot.Session.service;

import com.adeli.adelispringboot.Session.entity.Session;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ISessionService {

    Page<Session> getAllSessions(int page, int size, String sort, String order);
    Optional<Session> getSessionByName(String name);

    List<Session> getSessionsByNameLike(String name);

    void createSession(Session session);
    Session endSession(Session session);
    Session findLastSession();


}
