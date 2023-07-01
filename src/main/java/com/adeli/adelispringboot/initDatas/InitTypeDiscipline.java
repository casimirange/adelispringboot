/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Discipline.entity.ETypeDiscipline;
import com.adeli.adelispringboot.Discipline.entity.TypeDiscipline;
import com.adeli.adelispringboot.Discipline.repository.ITypeDisciplineRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 * @author Casimir
 */

@Component
@AllArgsConstructor
@Order(7)
public class InitTypeDiscipline implements ApplicationRunner{

    private ITypeDisciplineRepo iTypeDisciplineRepo;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des types de discipline");
        TypeDiscipline abs = new TypeDiscipline(ETypeDiscipline.ABSENCE);
        TypeDiscipline ret = new TypeDiscipline(ETypeDiscipline.RETARD);
        TypeDiscipline trbl = new TypeDiscipline(ETypeDiscipline.TROUBLE);

        if (!iTypeDisciplineRepo.existsByName(ETypeDiscipline.ABSENCE)) {
            iTypeDisciplineRepo.save(abs);
        }
        if (!iTypeDisciplineRepo.existsByName(ETypeDiscipline.RETARD)) {
            iTypeDisciplineRepo.save(ret);
        }

        if (!iTypeDisciplineRepo.existsByName(ETypeDiscipline.TROUBLE)) {
            iTypeDisciplineRepo.save(trbl);
        }
               
    }
    
}
