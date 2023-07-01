package com.adeli.adelispringboot.Session.service.impl;

import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.ISessionRepo;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
import com.adeli.adelispringboot.Session.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionServiceImpl implements ISessionService {

    @Autowired
    ISessionRepo iSessionRepo;

    @Autowired
    IStatusSessionRepo iStatusSessionRepo;
    @Override
    public Page<Session> getAllSessions(int page, int size, String sort, String order) {
        return iSessionRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Optional<Session> getSessionByName(String name) {
        return iSessionRepo.getSessionByName(name);
    }

    @Override
    public List<Session> getSessionsByNameLike(String name) {
        return iSessionRepo.getSessionsByNameContains(name);
    }

    @Override
    public void createSession(Session session) {
        iSessionRepo.save(session);
    }

    @Override
    public Session endSession(Session session) {
        SessionStatus sessionStatus = iStatusSessionRepo.findByName(EStatusSession.TERMINEE)
                .orElseThrow(() -> new ResourceNotFoundException("Ce statut " + EStatusSession.TERMINEE + " n'existe pas"));
        session.setStatus(sessionStatus);
        return iSessionRepo.save(session);
    }

    @Override
    public Session findLastSession() {
        return iSessionRepo.findFirstByOrderByIdDesc();
    }


}
