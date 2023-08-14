/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Prêts.entity.EStatusPret;
import com.adeli.adelispringboot.Prêts.entity.StatutPret;
import com.adeli.adelispringboot.Prêts.service.IStatusPretRepo;
import com.adeli.adelispringboot.Users.entity.EStatusUser;
import com.adeli.adelispringboot.Users.entity.StatusUser;
import com.adeli.adelispringboot.Users.repository.IStatusUserRepo;
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
@Order(9)
public class InitPretStatus implements ApplicationRunner{

    private IStatusPretRepo iStatusPretRepo;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des status prêt");
        StatutPret encours = new StatutPret(EStatusPret.EN_COURS);
        StatutPret acheve = new StatutPret(EStatusPret.ACHEVE);
        StatutPret innacheve = new StatutPret(EStatusPret.INNACHEVE);

        if (!iStatusPretRepo.existsByName(EStatusPret.EN_COURS)) {
            iStatusPretRepo.save(encours);
        }

        if (!iStatusPretRepo.existsByName(EStatusPret.INNACHEVE)) {
            iStatusPretRepo.save(innacheve);
        }

        if (!iStatusPretRepo.existsByName(EStatusPret.ACHEVE)) {
            iStatusPretRepo.save(acheve);
        }
    }
    
}
