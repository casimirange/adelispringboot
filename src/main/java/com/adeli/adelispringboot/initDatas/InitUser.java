/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.init;

import com.example.demo.entity.User;
import com.example.demo.models.RoleName;
import com.example.demo.repository.RoleRepository;
import com.example.demo.entity.Roles;
import com.example.demo.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
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
@Order(2)
public class InitUser implements ApplicationRunner{
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    UserRepository utilisateurRepository;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation de l'user");        
        String username = "ouandji casimir";
        String email = "ouandjicasimir@gmail.com";

        if (utilisateurRepository.existsByUsername(username)) {
            System.out.println("Fail -> Username is already taken!");
        }else if (utilisateurRepository.existsByEmail(email)) {
          System.out.println("Fail -> Email is already in use!");
        } else{
               
        String x = "32494043000";
        User user = new User();
        user.setName("Casimir Ouandji");
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encoder.encode("ange3000"));
        user.setTel(x);
        user.setEtat(true); 
        Set<Roles> roles = new HashSet<>();
       
        Roles super_adminRole = roleRepository.findByName(RoleName.ROLE_SUPER_ADMIN)
            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
        roles.add(super_adminRole);
        
        user.setRoles(roles);
                    System.out.println("userssss \n"+user);
            utilisateurRepository.save(user);
            System.out.println("Utilisateur enregistré");
        }
    }
    
}
