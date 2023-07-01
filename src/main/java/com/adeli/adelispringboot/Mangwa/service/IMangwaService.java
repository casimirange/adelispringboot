package com.adeli.adelispringboot.Seance.service;

import com.adeli.adelispringboot.Seance.entity.Seance;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ISeanceService {
    Page<Seance> getAllSeances(int page, int size, String sort, String order);

    void createSeance(Seance seance);

    Seance getById(Long id);
}
