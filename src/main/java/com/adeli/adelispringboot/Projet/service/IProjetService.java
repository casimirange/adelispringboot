package com.adeli.adelispringboot.Projet.service;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Projet.entity.Projet;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IProjetService {
    Page<Projet> getAllProjet(String member, LocalDate date, String type, int page, int size, String sort, String order);

    Page<Projet> getProjetBySeance(Long idSeance, int page, int size, String sort, String order);

    void createProjet(Projet projet);
    void createProjets(List<Projet> projet);
    Projet getById(Long id);
    Projet getByMotifAndDateType(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction);
    void deleteProjets(Long id);
    Double soldeProjet();
}
