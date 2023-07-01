package com.adeli.adelispringboot.Discipline.service;

import com.adeli.adelispringboot.Discipline.entity.Discipline;
import org.springframework.data.domain.Page;

public interface IDisciplineService {

    Page<Discipline> getAllDiscipline(int page, int size, String sort, String order);

    Page<Discipline> getDisciplinesBySeance(Long idSeance, int page, int size, String sort, String order);

    void createDiscipline(Discipline Discipline);

    Discipline getById(Long id);


}
