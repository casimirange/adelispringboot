/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;


import com.adeli.adelispringboot.Session.entity.EStatusSession;
import com.adeli.adelispringboot.Session.entity.SessionStatus;
import com.adeli.adelispringboot.Session.repository.IStatusSessionRepo;
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
@Order(5)
public class InitSessionStatus implements ApplicationRunner {

    private IStatusSessionRepo iStatusSessionRepo;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des status de la session");
        SessionStatus create = new SessionStatus(EStatusSession.CREEE);
        SessionStatus close = new SessionStatus(EStatusSession.TERMINEE);

        if (!iStatusSessionRepo.existsByName(EStatusSession.CREEE)) {
            iStatusSessionRepo.save(create);
        }
        if (!iStatusSessionRepo.existsByName(EStatusSession.TERMINEE)) {
            iStatusSessionRepo.save(close);
        }
               
    }
    
}
