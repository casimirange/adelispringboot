package com.adeli.adelispringboot.Prêts.service;


import com.adeli.adelispringboot.Prêts.entity.Prets;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import org.springframework.data.domain.Page;

public interface IPretService {
    Page<Prets> getAllPret(int page, int size, String sort, String order);

    Page<Prets> getPretBySeance(Long idSeance, int page, int size, String sort, String order);

    void createPret(Prets prets);

    void rembourserPret(Prets prets);

    Prets getById(Long id);
}
