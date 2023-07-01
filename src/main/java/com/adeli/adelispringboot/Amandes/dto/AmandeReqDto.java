package com.adeli.adelispringboot.Amandes.dto;

import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Data
public class AmandeReqDto {
    private double montant;

    private String motif;

    private Long idSeance;

    public Long idUser;

    public boolean pay;
}
