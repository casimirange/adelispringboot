package com.adeli.adelispringboot.Amandes.service.impl;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Amandes.repository.IAmandeRepo;
import com.adeli.adelispringboot.Amandes.service.IAmandeService;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
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
public class AmandeServiceImpl implements IAmandeService {

    @Autowired
    IAmandeRepo iAmandeRepo;

    @Autowired
    ISeanceService iSeanceService;

    @Override
    public Page<Amande> getAllAmandes(int page, int size, String sort, String order) {
        return iAmandeRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Amande> getAmandesBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        TontineResDto tontineResDto;
        Page<Amande> amandes = iAmandeRepo.findBySeance(seance, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return amandes;
    }

    @Override
    public void createAmande(Amande Amande) {
        iAmandeRepo.save(Amande);
    }
}
