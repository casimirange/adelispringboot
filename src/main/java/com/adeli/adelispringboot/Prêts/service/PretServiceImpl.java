package com.adeli.adelispringboot.Prêts.service;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Prêts.repository.PretRepository;
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
public class PretServiceImpl implements IPretService {
    @Autowired
    TontineRepository tontineRepository;

    @Autowired
    PretRepository pretRepository;
    @Autowired
    ISeanceService iSeanceService;
    @Autowired
    ITontineService iTontineService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    double x, y, z, t;

    @Override
    public Page<Prets> getAllPret(String member, LocalDate date, String type, int page, int size, String sort, String order) {
        Specification<Prets> specification = ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (member != null && !member.isEmpty()){
                Users users = iUserService.getById(Long.parseLong(member));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("user")),  users));
            }

            if (type != null && !type.isEmpty()){
                Optional<TypeTransaction> typeTransaction2 = iStatusTransactionRepo.findByName(EStatusTransaction.valueOf(type));
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("typeTransaction")), typeTransaction2.map(TypeTransaction::getId).orElse(null)));
            }

            if (date != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("date").as(String.class)), date.toString() + '%'));
            }
            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        });
        Page<Prets> tontines = pretRepository.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return tontines;
    }

    @Override
    @Transactional
    public Page<Prets> getPretBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        TontineResDto  tontineResDto;
        Page<Prets> tontineList = pretRepository.findBySeance(seance, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
//        List<TontineResDto> tontineResDtoList = new ArrayList<>();
//        for (Tontine tontine: tontineList){
//            tontineResDto = new TontineResDto();
//            tontineResDto.setId(tontine.getIdTontine());
//            tontineResDto.setDescription(tontine.getDescription());
//            tontineResDto.setMontant(tontine.getMontant());
//            tontineResDto.setTypeTransaction(tontine.getTypeTransaction());
//            tontineResDto.setUser(tontine.getUser());
//            tontineResDto.setSeance(tontine.getSeance());
//            tontineResDto.setCreatedAt(tontine.getCreatedAt());
//            tontineResDto.setUpdatedAt(tontine.getUpdatedAt());
//            tontineResDtoList.add(tontineResDto);
//        }
//        Page<Prets> orderPage = new PageImpl<>(tontineList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), tontineList.getTotalElements());
        return tontineList;
    }

    @Override
    public void createPret(Prets prets) {
        pretRepository.save(prets);
    }

    @Override
    public void rembourserPret(Prets prets) {
        pretRepository.save(prets);
    }

    @Override
    public void deletePret(Long id) {
        pretRepository.deleteById(id);
    }

    @Override
    public Prets getById(Long id) {
        return pretRepository.findById(id).get();
    }



}
