package com.kovid.doc_signature.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kovid.doc_signature.dto.DocumentResponse;
import com.kovid.doc_signature.model.Document;
import com.kovid.doc_signature.service.DocumentService;

@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService){
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId
    ) throws Exception {

        Document document = documentService.uploadDocument(file, userId);

        return ResponseEntity.ok(DocumentResponse.from(document));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @RequestParam("userId") Long userId
    ) {

        List<DocumentResponse> documents = documentService.getDocuments(userId)
                .stream()
                .map(DocumentResponse::from)
                .toList();

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long documentId
    ) {

        Document document = documentService.getDocument(documentId);

        return ResponseEntity.ok(DocumentResponse.from(document));
    }

    @GetMapping("/{documentId}/preview")
    public ResponseEntity<Resource> previewDocument(
            @PathVariable Long documentId
    ) throws Exception {

        Resource resource = documentService.getDocumentPreview(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
