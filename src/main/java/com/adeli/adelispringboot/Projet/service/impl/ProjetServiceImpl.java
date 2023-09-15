package com.adeli.adelispringboot.Projet.service.impl;

import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
import com.adeli.adelispringboot.Projet.entity.Projet;
import com.adeli.adelispringboot.Projet.repository.IProjetRepository;
import com.adeli.adelispringboot.Projet.service.IProjetService;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.service.ISeanceService;
import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.service.IUserService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
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
@RequiredArgsConstructor
public class ProjetServiceImpl implements IProjetService {
    final IProjetRepository iRetenueRepo;

    final ISeanceService iSeanceService;

    double x, y, z, t;

    final IUserService iUserService;

    final IStatusTransactionRepo iStatusTransactionRepo;

    @Override
    public Page<Projet> getAllProjet(String member, LocalDate date, String type, int page, int size, String sort, String order) {
        Specification<Projet> specification = ((root, query, criteriaBuilder) -> {

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
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        return iRetenueRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
    }

    @Override
    public Page<Projet> getProjetBySeance(Long idSeance, int page, int size, String sort, String order) {
        Seance seance = iSeanceService.getById(idSeance);
        Page<Projet> retenues = iRetenueRepo.findByDate(seance.getDate(), PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
        return retenues;
    }

    @Override
    public void createProjet(Projet projet) {
        iRetenueRepo.save(projet);
    }

    @Override
    public void createProjets(List<Projet> projet) {
        iRetenueRepo.saveAll(projet);
    }

    @Override
    public Projet getById(Long id) {
        return iRetenueRepo.findById(id).get();
    }

    @Override
    public Projet getByMotifAndDateType(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction) {
        return null;
    }

//    @Override
//    public Projet getByMotifAndDateType(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction) {
//        return iRetenueRepo.findRetenueByMotifAndMontantAndDateAndTypeTransaction(motif, montant, date, typeTransaction);
//    }

    @Override
    public void deleteProjets(Long id) {
        iRetenueRepo.deleteById(id);
    }

    @Override
    public Double soldeProjet() {
        List<JSONObject> p = iRetenueRepo.soldeProjet();

        List<JSONObject> depot = p.stream().filter(v-> v.getAsString("name").equals("DEPOT"))
                .collect(Collectors.toList());
        List<JSONObject> retrait = p.stream().filter(v-> v.getAsString("name").equals("RETRAIT"))
                .collect(Collectors.toList());
        List<JSONObject> pret = p.stream().filter(v-> v.getAsString("name").equals("PRET"))
                .collect(Collectors.toList());
        List<JSONObject> remb = p.stream().filter(v-> v.getAsString("name").equals("REMBOURSEMENT"))
                .collect(Collectors.toList());

        x = 0; y = 0; z = 0; t = 0;
        if (depot.size() == 1){x = depot.get(0).getAsNumber("montant").doubleValue();}
        if (pret.size() == 1){z = pret.get(0).getAsNumber("montant").doubleValue();}
        if (remb.size() == 1){t = remb.get(0).getAsNumber("montant").doubleValue();}
        if (retrait.size() == 1){y = retrait.get(0).getAsNumber("montant").doubleValue();}

        double solde =  x - y + t - z;
        BigDecimal bd = new BigDecimal(solde);
        bd = bd.setScale(2, RoundingMode.HALF_DOWN);
        solde = bd.doubleValue();
        return solde;
    }
}
