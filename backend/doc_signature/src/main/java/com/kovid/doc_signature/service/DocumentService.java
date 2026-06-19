package com.kovid.doc_signature.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kovid.doc_signature.model.Document;
import com.kovid.doc_signature.model.User;
import com.kovid.doc_signature.repository.DocumentRepository;
import com.kovid.doc_signature.repository.UserRepository;


@Service
public class DocumentService {

    
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;


    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            AuditLogService auditLogService
    ){

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;

    }

    public Document uploadDocument(
            MultipartFile file,
            Long userId
    ) throws IOException {


        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );


        String folder = "uploads/";

        File directory = new File(folder);


        if(!directory.exists()){

            directory.mkdir();

        }


        String path = folder + file.getOriginalFilename();


        file.transferTo(
                new File(path).getAbsoluteFile()
        );



        Document document = Document.builder()

                .fileName(file.getOriginalFilename())

                .filePath(path)

                .uploadedAt(LocalDateTime.now())

                .status("PENDING")

                .owner(user)

                .build();



     Document savedDocument = documentRepository.save(document);

auditLogService.logAction(
        "DOCUMENT_UPLOADED",
        user,
        savedDocument
);

return savedDocument;

    }

    public List<Document> getDocuments(Long userId){

        return documentRepository.findByOwnerId(userId);

    }

    public Document getDocument(Long documentId) {

        return documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found")
                );

    }

    public Resource getDocumentPreview(Long documentId) throws IOException {

        Document document = getDocument(documentId);

        Path path = Path.of(document.getFilePath()).toAbsolutePath().normalize();

        if (!Files.exists(path) || !Files.isReadable(path)) {

            throw new RuntimeException("Document file not found");

        }

        return new UrlResource(path.toUri());

    }


}
