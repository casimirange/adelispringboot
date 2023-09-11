package com.adeli.adelispringboot.Tontine.service;


import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ITontineService {
    Page<Tontine> getAllTontine(int page, int size, String sort, String order);

    Page<TontineResDto> getTontinesBySeance(Long idSeance, int page, int size, String sort, String order);
    Tontine getTontineByDescriptionAndSeance(Seance seance, String description);
    Tontine getTontineByDescriptionAndSeanceAndAmount(Seance seance, String description, Double montant);

    void createTontines(List<Tontine> tontine);
    void createTontine(Tontine tontine);
    void deleteTontine(Tontine tontine);

    boolean existByDate(Seance seance, TypeTransaction typeTransaction);

    Tontine getById(Long id);

    Double soldeTontine();
}
