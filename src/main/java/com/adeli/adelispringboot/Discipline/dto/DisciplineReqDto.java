package com.adeli.adelispringboot.Discipline.dto;

import lombok.Data;


@Data
public class DisciplineReqDto {

    private String sanction;

    public Long idUser;

    private Long idSeance;
}
