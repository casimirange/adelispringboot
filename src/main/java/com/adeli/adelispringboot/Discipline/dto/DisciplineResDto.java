package com.adeli.adelispringboot.Discipline.dto;

import lombok.Data;


@Data
public class DisciplineResDto {

    private String status;

    private String sanction;

    public String user;

    private Long seance;
}
