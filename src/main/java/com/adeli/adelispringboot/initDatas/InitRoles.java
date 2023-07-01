/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Users.entity.ERole;
import com.adeli.adelispringboot.Users.entity.RoleUser;
import com.adeli.adelispringboot.Users.repository.IRoleUserRepo;
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
@Order(1)
public class InitRoles implements ApplicationRunner{

    private IRoleUserRepo roleRepository;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des roles");        
        RoleUser superadmin = new RoleUser(ERole.ROLE_SUPERADMIN);
        RoleUser user = new RoleUser(ERole.ROLE_USER);

        if (!roleRepository.existsByName(ERole.ROLE_SUPERADMIN)) {
            roleRepository.save(superadmin);
        }
        if (!roleRepository.existsByName(ERole.ROLE_USER)) {
            roleRepository.save(user);
        }
               
    }
    
}
