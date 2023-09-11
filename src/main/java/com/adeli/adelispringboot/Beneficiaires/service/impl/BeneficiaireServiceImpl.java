package com.adeli.adelispringboot.Beneficiaires.service.impl;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Amandes.repository.IAmandeRepo;
import com.adeli.adelispringboot.Beneficiaires.entity.Beneficiaire;
import com.adeli.adelispringboot.Beneficiaires.repository.BeneficiaireRepository;
import com.adeli.adelispringboot.Beneficiaires.service.IBeneficiaireService;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BeneficiaireServiceImpl implements IBeneficiaireService {

    @Autowired
    BeneficiaireRepository beneficiaireRepository;

    @Autowired
    ISeanceService iSeanceService;

    @Override
    public Page<Beneficiaire> getAllBeneficiaires(int page, int size, String sort, String order) {
        return beneficiaireRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Beneficiaire getBeneficiaire(Long id) {
        return beneficiaireRepository.findById(id).get();
    }

    @Override
    public Page<Beneficiaire> getBeneficiaireBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        Page<Beneficiaire> benefs = beneficiaireRepository.findBySeance(seance, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return benefs;
    }

    @Override
    public void createBeneficiaire(Beneficiaire beneficiaire) {
        beneficiaireRepository.save(beneficiaire);
    }

    @Override
    public void deleteBeneficiaire(Beneficiaire beneficiaire) {
        beneficiaireRepository.deleteById(beneficiaire.getId());
    }
}
