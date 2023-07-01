package com.adeli.adelispringboot.Seance.dto;

import com.adeli.adelispringboot.Session.entity.Session;
import com.adeli.adelispringboot.Tontine.entity.Tontine;
import com.adeli.adelispringboot.Users.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class SeanceReqDto {
    @Schema(required = true, example = "17/12/2022", description = "date à laquelle s'est tenue la reunion")
    @NotNull(message = "{date.required}")
    private LocalDate date;

    @Schema(required = true, example = "5 €", description = "membre chez qui se déroule la reunion")
    @NotNull(message = "{users.required}")
    private Long users;

//    @Schema(required = true, example = "5 €", description = "session en cours")
//    @NotNull(message = "{session.required}")
//    public Session session;
}
