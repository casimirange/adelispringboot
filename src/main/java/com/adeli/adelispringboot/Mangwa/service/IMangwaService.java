package com.adeli.adelispringboot.Mangwa.service;

import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import org.springframework.data.domain.Page;

public interface IMangwaService {
    Page<Retenue> getAllMangwa(int page, int size, String sort, String order);

    Page<Retenue> getMangwaBySeance(Long idSeance, int page, int size, String sort, String order);

    void createMangwa(Retenue retenue);
    Retenue getById(Long id);
    Double soldeMangwa();
}
