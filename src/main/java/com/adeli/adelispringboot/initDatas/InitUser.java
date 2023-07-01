/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Users.entity.*;
import com.adeli.adelispringboot.Users.repository.IRoleUserRepo;
import com.adeli.adelispringboot.Users.repository.IStatusUserRepo;
import com.adeli.adelispringboot.Users.repository.ITypeAccountRepository;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Casimir
 */

@Component
@Order(4)
public class InitUser implements ApplicationRunner{
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    IRoleUserRepo roleRepository;

    @Autowired
    ITypeAccountRepository iTypeAccountRepository;
    
    @Autowired
    IUserRepo utilisateurRepository;
    @Autowired
    private IStatusUserRepo iStatusUserRepo;

    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation de l'user");

        String email = "ouandji.casimir@gmail.com";
        String phone = "693764263";
        if (utilisateurRepository.existsByEmail(email)) {
          System.out.println("Fail -> Email is already in use!");
        }
        if (utilisateurRepository.existsByTelephone(phone)) {
          System.out.println("Fail -> Phone is already in use!");
        } else{

        double montant = 500;

        Users user = new Users();
        user.setLastName("Casimir");
        user.setFirstName("Ouandji");
        user.setEmail(email);
        user.setMontant(montant);
        user.setPassword(encoder.encode("Ange3000-"));
        user.setTelephone(phone);
        StatusUser statusUser = iStatusUserRepo.findByName(EStatusUser.USER_ENABLED);
        user.setStatus(statusUser);
        user.setCreatedDate(LocalDateTime.now());
        Set<RoleUser> roles = new HashSet<>();
        TypeAccount rolePresident = iTypeAccountRepository.findByName(ETypeAccount.PRESIDENT).get();
        user.setTypeAccount(rolePresident);

        RoleUser super_adminRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
        roles.add(super_adminRole);
        
        user.setRoles(roles);
                    System.out.println("userssss \n"+user);
            utilisateurRepository.save(user);
            System.out.println("Utilisateur enregistré");
        }
    }
    
}
