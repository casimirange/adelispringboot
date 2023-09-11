package com.adeli.adelispringboot.Beneficiaires.service;
import com.adeli.adelispringboot.Amandes.entity.Amande;
import com.adeli.adelispringboot.Beneficiaires.entity.Beneficiaire;
import org.springframework.data.domain.Page;

public interface IBeneficiaireService {
    Page<Beneficiaire> getAllBeneficiaires(int page, int size, String sort, String order);
    Beneficiaire getBeneficiaire(Long id);
    Page<Beneficiaire> getBeneficiaireBySeance(Long idSeance, int page, int size, String sort, String order);
    void createBeneficiaire(Beneficiaire beneficiaire);
    void deleteBeneficiaire(Beneficiaire beneficiaire);
}
