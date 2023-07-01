/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adeli.adelispringboot.initDatas;

import com.adeli.adelispringboot.Discipline.entity.ETypeDiscipline;
import com.adeli.adelispringboot.Discipline.entity.TypeDiscipline;
import com.adeli.adelispringboot.Discipline.repository.ITypeDisciplineRepo;
import com.adeli.adelispringboot.Document.entity.ETypeDocument;
import com.adeli.adelispringboot.Document.entity.TypeDocument;
import com.adeli.adelispringboot.Document.repository.ITypeDocumentRepo;
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
@Order(8)
public class InitTypeDocument implements ApplicationRunner{

    private ITypeDocumentRepo iTypeDocumentRepo;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {;
        System.out.println("initialisation des types de document");
        TypeDocument abs = new TypeDocument(null, ETypeDocument.DELIVERY);
        TypeDocument ret = new TypeDocument(null, ETypeDocument.INVOICE);

        if (!iTypeDocumentRepo.existsByName(ETypeDocument.DELIVERY)) {
            iTypeDocumentRepo.save(abs);
        }
        if (!iTypeDocumentRepo.existsByName(ETypeDocument.INVOICE)) {
            iTypeDocumentRepo.save(ret);
        }
               
    }
    
}
