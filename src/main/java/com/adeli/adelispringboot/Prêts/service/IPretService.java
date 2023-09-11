package com.adeli.adelispringboot.Prêts.service;


import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface IPretService {
    Page<Prets> getAllPret(String member, LocalDate date, String type, int page, int size, String sort, String order);

    Page<Prets> getPretBySeance(Long idSeance, int page, int size, String sort, String order);

    void createPret(Prets prets);

    void rembourserPret(Prets prets);
    void deletePret(Long id);

    Prets getById(Long id);
}
