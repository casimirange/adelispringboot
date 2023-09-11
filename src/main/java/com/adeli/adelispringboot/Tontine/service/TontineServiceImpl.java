package com.adeli.adelispringboot.Tontine.service;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Tontine.repository.TontineRepository;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TontineServiceImpl implements ITontineService{
    @Autowired
    TontineRepository tontineRepository;
    @Autowired
    ISeanceService iSeanceService;

    double x, y, z, t;

    @Override
    public Page<Tontine> getAllTontine(int page, int size, String sort, String order) {
        Page<Tontine> tontines = tontineRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return tontines;
    }

    @Override
    @Transactional
    public Page<TontineResDto> getTontinesBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        TontineResDto  tontineResDto;
        Page<Tontine> tontineList = tontineRepository.findBySeance(seance, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        List<TontineResDto> tontineResDtoList = new ArrayList<>();
        for (Tontine tontine: tontineList){
            tontineResDto = new TontineResDto();
            tontineResDto.setId(tontine.getIdTontine());
            tontineResDto.setDescription(tontine.getDescription());
            tontineResDto.setMontant(tontine.getMontant());
            tontineResDto.setTypeTransaction(tontine.getTypeTransaction());
            tontineResDto.setUser(tontine.getUser());
            tontineResDto.setSeance(tontine.getSeance());
            tontineResDto.setCreatedAt(tontine.getCreatedAt());
            tontineResDto.setUpdatedAt(tontine.getUpdatedAt());
            tontineResDtoList.add(tontineResDto);
        }
        Page<TontineResDto> orderPage = new PageImpl<>(tontineResDtoList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), tontineList.getTotalElements());
        return orderPage;
    }

    @Override
    public Tontine getTontineByDescriptionAndSeance(Seance seance, String description) {
        return tontineRepository.findTontineBySeanceAndDescription(seance, description);
    }

    @Override
    public Tontine getTontineByDescriptionAndSeanceAndAmount(Seance seance, String description, Double montant) {
        return tontineRepository.findTontineBySeanceAndDescriptionAndMontant(seance, description, montant);
    }

    @Override
    public void createTontines(List<Tontine> tontine) {
        tontineRepository.saveAll(tontine);
    }

    @Override
    public void createTontine(Tontine tontine) {
        tontineRepository.save(tontine);
    }

    @Override
    public void deleteTontine(Tontine tontine) {
        tontineRepository.deleteById(tontine.getIdTontine());
    }

    @Override
    public boolean existByDate(Seance seance, TypeTransaction typeTransaction) {
        return tontineRepository.existsBySeanceAndTypeTransaction(seance, typeTransaction);
    }

    @Override
    public Tontine getById(Long id) {
        return tontineRepository.findById(id).get();
    }

    @Override
    public Double soldeTontine() {
        List<JSONObject> p = tontineRepository.soldeTontine();
        List<JSONObject> depot = p.stream().filter(v-> v.getAsString("name").equals("DEPOT")).collect(Collectors.toList());
        List<JSONObject> retrait = p.stream().filter(v-> v.getAsString("name").equals("RETRAIT")).collect(Collectors.toList());
        List<JSONObject> pret = p.stream().filter(v-> v.getAsString("name").equals("PRET")).collect(Collectors.toList());
        List<JSONObject> remboursement = p.stream().filter(v-> v.getAsString("name").equals("REMBOURSEMENT")).collect(Collectors.toList());

        x = 0; y = 0; z= 0; t = 0;
        if (depot.size() == 1){x = depot.get(0).getAsNumber("montant").doubleValue();}
        if (remboursement.size() == 1){y = remboursement.get(0).getAsNumber("montant").doubleValue();}
        if (retrait.size() == 1){z = retrait.get(0).getAsNumber("montant").doubleValue();}
        if (pret.size() == 1){t = pret.get(0).getAsNumber("montant").doubleValue();}

        double solde =  x + y - z - t;
        BigDecimal bd = new BigDecimal(solde);
        bd = bd.setScale(2, RoundingMode.HALF_DOWN);
        solde = bd.doubleValue();
        return solde;
    }
}
