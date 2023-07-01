package com.adeli.adelispringboot.Document.repository;

import com.adeli.adelispringboot.Document.entity.ETypeDocument;
import com.adeli.adelispringboot.Document.entity.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "typeDocument")
public interface ITypeDocumentRepo extends JpaRepository<TypeDocument, Long> {
	Optional<TypeDocument> findByName(ETypeDocument name);

}
