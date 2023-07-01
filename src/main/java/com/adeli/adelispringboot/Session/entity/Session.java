package com.adeli.adelispringboot.Session.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double mangwa;

    @Column(nullable = false)
    private LocalDate beginDate;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private double tax;

    @ManyToOne
    private SessionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
