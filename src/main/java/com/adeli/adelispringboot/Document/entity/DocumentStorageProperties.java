package com.adeli.adelispringboot.Document.entity;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;


import javax.persistence.*;


@Entity
@Getter
@Setter
@ConfigurationProperties(prefix = "file")
@NoArgsConstructor
@EqualsAndHashCode(of = { "documentId", "fileName", "documentType", "documentFormat", "type", "seance" })
@Table(name = "seance_documents")
public class DocumentStorageProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long documentId;

    private String fileName;

    private String documentType;

    private String documentFormat;

    @ManyToOne
    private TypeDocument type;

    @Transient
    private String uploadDir;

    @Column(nullable = false, name = "id_seance")
    private Long seance;
}
