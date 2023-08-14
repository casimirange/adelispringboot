package com.adeli.adelispringboot.Prêts.dto;

import com.adeli.adelispringboot.Mangwa.entity.TypeTransaction;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Null;
import java.time.LocalDate;

@Data
public class PretReqDto {
    double montant_prete;

    double montant_rembourse;

    Long idUser;

    Long idSeance;

    LocalDate dateRemboursement;

    boolean type;
}
