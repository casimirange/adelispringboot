package com.adeli.adelispringboot.Document.repository;

import com.adeli.adelispringboot.Document.entity.DocumentStorageProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IDocumentStoragePropertiesRepo extends JpaRepository<DocumentStorageProperties, Long> {

    @Query(value = "Select * from seance_documents where id_seance = ?1 and document_type = ?2 and type_id = ?3", nativeQuery = true)
    DocumentStorageProperties checkDocumentByOrderId(Long idOrder, String docType, Long type);

    @Query(value = "Select file_name from seance_documents a where id_seance = ?1 and document_type = ?2 and type_id = ?3", nativeQuery = true)
    String getUploadDocumnetPath(Long idOrder, String docType, Long type);

    @Query(value = "Select * from communique_documents where id_communique = ?1 and document_type = ?2 and type_id = ?3", nativeQuery = true)
    DocumentStorageProperties checkDocumentByCommuniqueId(Long idOrder, String docType, Long type);

    @Query(value = "Select file_name from communique_documents a where id_communique = ?1 and document_type = ?2 and type_id = ?3", nativeQuery = true)
    String getUploadCommuniqueDocumnetPath(Long idOrder, String docType, Long type);
}
