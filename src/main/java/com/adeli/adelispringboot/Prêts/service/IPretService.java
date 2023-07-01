package com.adeli.adelispringboot.Tontine.service;


import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import org.springframework.data.domain.Page;

public interface ITontineService {
    Page<Tontine> getAllTontine(int page, int size, String sort, String order);

    Page<TontineResDto> getTontinesBySeance(Long idSeance, int page, int size, String sort, String order);

    void createTontine(Tontine tontine);

    Tontine getById(Long id);

    Double soldeTontine();
}
