package com.adeli.adelispringboot.Discipline.repository;

import com.adeli.adelispringboot.Discipline.entity.ETypeDiscipline;
import com.adeli.adelispringboot.Discipline.entity.TypeDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITypeDisciplineRepo extends JpaRepository<TypeDiscipline,Long> {
    Optional<TypeDiscipline> findByName(ETypeDiscipline name);
    Optional<TypeDiscipline> getByName(String name);
    boolean existsByName(ETypeDiscipline name);
}
