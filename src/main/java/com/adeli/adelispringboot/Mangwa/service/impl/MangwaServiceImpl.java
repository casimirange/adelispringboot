package com.adeli.adelispringboot.Mangwa.service.impl;

import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IRetenueRepository;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Mangwa.service.IMangwaService;
import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.service.IUserService;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.w3c.dom.stylesheets.LinkStyle;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MangwaServiceImpl implements IMangwaService {
    final IRetenueRepository iRetenueRepo;

    final ISeanceService iSeanceService;

    double x, y;

    final IUserService iUserService;

    final IStatusTransactionRepo iStatusTransactionRepo;

    @Override
    public Page<Retenue> getAllMangwa(String member, LocalDate date, String type, int page, int size, String sort, String order) {
        Specification<Retenue> specification = ((root, query, criteriaBuilder) -> {

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
        return iRetenueRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Retenue> getMangwaBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        Page<Retenue> retenues = iRetenueRepo.findByDate(seance.getDate(), PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return retenues;
    }

    @Override
    public void createMangwa(Retenue retenue) {
        iRetenueRepo.save(retenue);
    }

    @Override
    public void createMangwas(List<Retenue> retenue) {
        iRetenueRepo.saveAll(retenue);
    }

    @Override
    public Retenue getById(Long id) {
        return iRetenueRepo.findById(id).get();
    }

    @Override
    public Retenue getByMotifAndDateType(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction) {
        return iRetenueRepo.findRetenueByMotifAndMontantAndDateAndTypeTransaction(motif, montant, date, typeTransaction);
    }

    @Override
    public void deleteMangwas(Long id) {
        iRetenueRepo.deleteById(id);
    }

    @Override
    public Double soldeMangwa() {
        List<JSONObject> p = iRetenueRepo.soldeMangwa();

        List<JSONObject> depot = p.stream().filter(v-> v.getAsString("name").equals("DEPOT"))
                .collect(Collectors.toList());
        List<JSONObject> retrait = p.stream().filter(v-> v.getAsString("name").equals("RETRAIT"))
                .collect(Collectors.toList());

        x = 0; y = 0;
        if (depot.size() == 1){x = depot.get(0).getAsNumber("montant").doubleValue();}
        if (retrait.size() == 1){y = retrait.get(0).getAsNumber("montant").doubleValue();}

        double solde =  x - y;
        BigDecimal bd = new BigDecimal(solde);
        bd = bd.setScale(2, RoundingMode.HALF_DOWN);
        solde = bd.doubleValue();
        return solde;
    }
}
