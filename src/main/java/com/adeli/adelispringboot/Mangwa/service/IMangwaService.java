package com.adeli.adelispringboot.Mangwa.service;

import com.adeli.adelispringboot.Mangwa.entity.Retenue;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IMangwaService {
    Page<Retenue> getAllMangwa(String member, LocalDate date, String type, int page, int size, String sort, String order);

    Page<Retenue> getMangwaBySeance(Long idSeance, int page, int size, String sort, String order);

    void createMangwa(Retenue retenue);
    void createMangwas(List<Retenue> retenue);
    Retenue getById(Long id);
    Retenue getByMotifAndDateType(String motif, Double montant, LocalDate date, TypeTransaction typeTransaction);
    void deleteMangwas(Long id);
    Double soldeMangwa();
}
