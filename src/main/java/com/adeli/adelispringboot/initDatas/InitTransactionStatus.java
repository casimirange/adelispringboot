/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Mangwa.entity.EStatusTransaction;
import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Mangwa.repository.IStatusTransactionRepo;
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
@Order(6)
public class InitTransactionStatus implements ApplicationRunner{

    private IStatusTransactionRepo iStatusTransactionRepo;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des status de transaction");
        TypeTransaction depot = new TypeTransaction(EStatusTransaction.DEPOT);
        TypeTransaction retrait = new TypeTransaction(EStatusTransaction.RETRAIT);
        TypeTransaction pret = new TypeTransaction(EStatusTransaction.PRET);
        TypeTransaction remboursement = new TypeTransaction(EStatusTransaction.REMBOURSEMENT);
        TypeTransaction np = new TypeTransaction(EStatusTransaction.NON_PAYE);

        if (!iStatusTransactionRepo.existsByName(EStatusTransaction.DEPOT)) {
            iStatusTransactionRepo.save(depot);
        }

        if (!iStatusTransactionRepo.existsByName(EStatusTransaction.RETRAIT)) {
            iStatusTransactionRepo.save(retrait);
        }

        if (!iStatusTransactionRepo.existsByName(EStatusTransaction.PRET)) {
            iStatusTransactionRepo.save(pret);
        }

        if (!iStatusTransactionRepo.existsByName(EStatusTransaction.REMBOURSEMENT)) {
            iStatusTransactionRepo.save(remboursement);
        }

        if (!iStatusTransactionRepo.existsByName(EStatusTransaction.NON_PAYE)) {
            iStatusTransactionRepo.save(np);
        }
               
    }
    
}
