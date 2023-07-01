/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Users.entity.ERole;
import com.adeli.adelispringboot.Users.entity.ETypeAccount;
import com.adeli.adelispringboot.Users.entity.RoleUser;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import com.adeli.adelispringboot.Users.repository.IRoleUserRepo;
import com.adeli.adelispringboot.Users.repository.ITypeAccountRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@Order(2)
public class InitTypeAccount implements ApplicationRunner{

    private ITypeAccountRepository iTypeAccountRepository;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des types de comptes");
        TypeAccount roleUser = new TypeAccount(ETypeAccount.ADHERANT);
        TypeAccount rolePresident = new TypeAccount(ETypeAccount.PRESIDENT);
        TypeAccount rolePorteParole = new TypeAccount(ETypeAccount.PORTE_PAROLE);
        TypeAccount roleTresorier = new TypeAccount(ETypeAccount.TRESORIER);
        TypeAccount roleSenceur = new TypeAccount(ETypeAccount.SENSCEUR);
        TypeAccount roleSecretaire = new TypeAccount(ETypeAccount.SECRETAIRE);
        TypeAccount roleCommissaire = new TypeAccount(ETypeAccount.COMISSAIRE_AU_COMPTE);

        if (!iTypeAccountRepository.existsByName(ETypeAccount.ADHERANT)) {
            iTypeAccountRepository.save(roleUser);
        }
        if (!iTypeAccountRepository.existsByName(ETypeAccount.PRESIDENT)) {
            iTypeAccountRepository.save(rolePresident);
        }

        if (!iTypeAccountRepository.existsByName(ETypeAccount.PORTE_PAROLE)) {
            iTypeAccountRepository.save(rolePorteParole);
        }

        if (!iTypeAccountRepository.existsByName(ETypeAccount.TRESORIER)) {
            iTypeAccountRepository.save(roleTresorier);
        }

        if (!iTypeAccountRepository.existsByName(ETypeAccount.SENSCEUR)) {
            iTypeAccountRepository.save(roleSenceur);
        }

        if (!iTypeAccountRepository.existsByName(ETypeAccount.SECRETAIRE)) {
            iTypeAccountRepository.save(roleSecretaire);
        }

        if (!iTypeAccountRepository.existsByName(ETypeAccount.COMISSAIRE_AU_COMPTE)) {
            iTypeAccountRepository.save(roleCommissaire);
        }
               
    }
    
}
