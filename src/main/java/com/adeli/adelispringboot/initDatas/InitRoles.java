/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.init;

import com.example.demo.models.RoleName;
import com.example.demo.repository.RoleRepository;
import com.example.demo.entity.Roles;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Casimir
 */

@Component
@Order(1)
public class InitRoles implements ApplicationRunner{

    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    UserRepository utilisateurRepository;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des roles");        
        Roles roleUser = new Roles(RoleName.ROLE_ADHERENT);
        Roles rolePresident = new Roles(RoleName.ROLE_PRESIDENT);
        Roles rolePorteParole = new Roles(RoleName.ROLE_PORTE_PAROLE);
        Roles roleSuperAdmin = new Roles(RoleName.ROLE_SUPER_ADMIN);
        Roles roleTresorier = new Roles(RoleName.ROLE_TRESORIER);
        Roles roleSenceur = new Roles(RoleName.ROLE_SENSCEUR);
        Roles roleSecretaire = new Roles(RoleName.ROLE_SECRETAIRE);
        Roles roleCommissaire = new Roles(RoleName.ROLE_COMISSAIRE_AU_COMPTE);

        if (!roleRepository.existsByName(RoleName.ROLE_ADHERENT)) {
            roleRepository.save(roleUser);
        }
        if (!roleRepository.existsByName(RoleName.ROLE_PRESIDENT)) {
            roleRepository.save(rolePresident);
        }

        if (!roleRepository.existsByName(RoleName.ROLE_PORTE_PAROLE)) {
            roleRepository.save(rolePorteParole);
        }

        if (!roleRepository.existsByName(RoleName.ROLE_TRESORIER)) {
            roleRepository.save(roleTresorier);
        }

        if (!roleRepository.existsByName(RoleName.ROLE_SUPER_ADMIN)) {
            roleRepository.save(roleSuperAdmin);
        }

        if (!roleRepository.existsByName(RoleName.ROLE_SENSCEUR)) {
            roleRepository.save(roleSenceur);
        }

        if (!roleRepository.existsByName(RoleName.ROLE_SECRETAIRE)) {
            roleRepository.save(roleSecretaire);
        }

        if (!roleRepository.existsByName(RoleName.ROLE_COMISSAIRE_AU_COMPTE)) {
            roleRepository.save(roleCommissaire);
        }
               
    }
    
}
