package com.adeli.adelispringboot.Discipline.service.impl;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Discipline.entity.Discipline;
import com.adeli.adelispringboot.Discipline.entity.ETypeDiscipline;
import com.adeli.adelispringboot.Discipline.entity.TypeDiscipline;
import com.adeli.adelispringboot.Discipline.repository.DisciplineRepository;
import com.adeli.adelispringboot.Discipline.repository.ITypeDisciplineRepo;
import com.adeli.adelispringboot.Discipline.service.IDisciplineService;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Tontine.repository.TontineRepository;
import com.adeli.adelispringboot.Tontine.service.ITontineService;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DisciplineServiceImpl implements IDisciplineService {
    @Autowired
    DisciplineRepository disciplineRepository;
    @Autowired
    ISeanceService iSeanceService;

    @Autowired
    IUserService iUserService;

    @Autowired
    ITypeDisciplineRepo iTypeDisciplineRepo;

    @Override
    public Page<Discipline> getAllDiscipline(String member, LocalDate date, String type, int page, int size, String sort, String order) {
        Specification<Discipline> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (member != null && !member.isEmpty()){
                Users users = iUserService.getById(Long.parseLong(member));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("user")),  users));
            }

            if (type != null && !type.isEmpty()){
                Optional<TypeDiscipline> typeDiscipline = iTypeDisciplineRepo.findByName(ETypeDiscipline.valueOf(type));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("typeDiscipline")), typeDiscipline.map(TypeDiscipline::getId).orElse(null)));
            }

            if (date != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("date").as(String.class)), date.toString() + '%'));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });
        Page<Discipline> disciplines = disciplineRepository.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return disciplines;
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
