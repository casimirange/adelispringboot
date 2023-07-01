package com.adeli.adelispringboot.Discipline.service.impl;

import com.adeli.adelispringboot.Discipline.entity.Discipline;
import com.adeli.adelispringboot.Discipline.repository.DisciplineRepository;
import com.adeli.adelispringboot.Discipline.service.IDisciplineService;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Tontine.repository.TontineRepository;
import com.adeli.adelispringboot.Tontine.service.ITontineService;
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
public class DisciplineServiceImpl implements IDisciplineService {
    @Autowired
    DisciplineRepository disciplineRepository;
    @Autowired
    ISeanceService iSeanceService;

    @Override
    public Page<Discipline> getAllDiscipline(int page, int size, String sort, String order) {
        Page<Discipline> tontines = disciplineRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return tontines;
    }

    @Override
    @Transactional
    public Page<Discipline> getDisciplinesBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        Page<Discipline> tontineList = disciplineRepository.findBySeance(seance, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return tontineList;
    }

    @Override
    public void createDiscipline(Discipline discipline) {
        disciplineRepository.save(discipline);
    }

    @Override
    public Discipline getById(Long id) {
        return disciplineRepository.findById(id).get();
    }

}
