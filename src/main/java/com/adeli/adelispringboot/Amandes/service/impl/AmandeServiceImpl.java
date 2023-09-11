package com.adeli.adelispringboot.Amandes.service.impl;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Amandes.repository.IAmandeRepo;
import com.adeli.adelispringboot.Amandes.service.IAmandeService;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AmandeServiceImpl implements IAmandeService {

    @Autowired
    IAmandeRepo iAmandeRepo;

    @Autowired
    ISeanceService iSeanceService;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStatusTransactionRepo iStatusTransactionRepo;

    @Override
    public Page<Amande> getAllAmandes(String member, LocalDate date, String type, int page, int size, String sort, String order) {
        Specification<Amande> specification = ((root, query, criteriaBuilder) -> {

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
        return iAmandeRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
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

    @Override
    public Amande getAmandeById(Long id) {
        return iAmandeRepo.findById(id).get();
    }

    @Override
    public void deleteAmande(Long id) {
        iAmandeRepo.deleteById(id);
    }
}
