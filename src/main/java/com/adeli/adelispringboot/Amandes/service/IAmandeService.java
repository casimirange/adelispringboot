package com.adeli.adelispringboot.Amandes.service;
import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Tontine.dto.TontineResDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAmandeService {
    Page<Amande> getAllAmandes(String member, LocalDate date, String type, int page, int size, String sort, String order);
    Page<Amande> getAmandesBySeance(Long idSeance, int page, int size, String sort, String order);
    void createAmande(Amande amande);
    Amande getAmandeById(Long id);
    void deleteAmande(Long id);
}
