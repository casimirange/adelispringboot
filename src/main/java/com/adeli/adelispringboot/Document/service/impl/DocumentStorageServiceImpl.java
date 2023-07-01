package com.adeli.adelispringboot.Document.service.impl;

import com.adeli.adelispringboot.Document.entity.DocumentStorageProperties;
import com.adeli.adelispringboot.Document.entity.TypeDocument;
import com.adeli.adelispringboot.Document.repository.IDocumentStoragePropertiesRepo;
import com.adeli.adelispringboot.Document.service.IDocumentStorageService;
import com.adeli.adelispringboot.Seance.entity.Seance;
import com.adeli.adelispringboot.Seance.repository.ISeanceRepository;
import com.adeli.adelispringboot.configuration.exception.DocumentsStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class DocumentStorageServiceImpl implements IDocumentStorageService {

    private final Path fileStorageLocation;
    @Autowired
    IDocumentStoragePropertiesRepo docStorageRepo;

    @Autowired
    ISeanceRepository iSeanceRepository;
    
    @Autowired
    public DocumentStorageServiceImpl(DocumentStorageProperties fileStorageProperties) {
        fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            throw new DocumentsStorageException("Could not create the directory where the uploaded files will be stored.", e);

        }
    }
    @Override
    @Transactional
    public String storeFile(MultipartFile file, Long idSeance, String docType, TypeDocument typeDocument) {
        Seance seance = iSeanceRepository.findById(idSeance).get();
        // Normalize file name
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = "";

        try {
            // Check if the file's name contains invalid characters
            if(originalFilename.contains("..")) {
                throw new DocumentsStorageException("Sorry! Filename contains invalid path sequence " + originalFilename);
            }
            String fileExtension = "";
            try {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } catch (Exception e) {
                fileExtension = "";
            }
            fileName = "CR_" + seance.getDate() + fileExtension;
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            DocumentStorageProperties doc = docStorageRepo.checkDocumentByOrderId(idSeance, docType, typeDocument.getId());
            if(doc != null) {
                doc.setDocumentFormat(file.getContentType());
                doc.setFileName(fileName);
                doc.setType(typeDocument);
                docStorageRepo.save(doc);
            } else {
                DocumentStorageProperties newDoc = new DocumentStorageProperties();
                newDoc.setSeance(idSeance);
                newDoc.setDocumentFormat(file.getContentType());
                newDoc.setFileName(fileName);
                newDoc.setType(typeDocument);
                newDoc.setDocumentType(docType);
                docStorageRepo.save(newDoc);
            }
            return fileName;
        } catch (IOException e) {
            throw new DocumentsStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    

    @Override
    @Transactional
    public Resource loadFileAsResource(String fileName) throws Exception {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }

        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    @Override
    public String getDocumentName(Long idOrder, String docType, Long type) {
        return docStorageRepo.getUploadDocumnetPath(idOrder, docType, type);
    }
}
