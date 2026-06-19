package com.kovid.doc_signature.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kovid.doc_signature.dto.SignatureRequest;
import com.kovid.doc_signature.model.Document;
import com.kovid.doc_signature.model.Signature;
import com.kovid.doc_signature.model.User;
import com.kovid.doc_signature.repository.DocumentRepository;
import com.kovid.doc_signature.repository.SignatureRepository;
import com.kovid.doc_signature.repository.UserRepository;

@Service
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PDFservice pdfService;
    private final AuditLogService auditLogService;
    
    public SignatureService(
            SignatureRepository signatureRepository,
            DocumentRepository documentRepository,
            UserRepository userRepository,
            PDFservice pdfService,
            AuditLogService auditLogService
    ) {
        this.signatureRepository = signatureRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
        this.auditLogService = auditLogService;
    }

    public Signature saveSignature(SignatureRequest request) {
        Document document = documentRepository.findById(request.documentId())
                .orElseThrow(() -> new RuntimeException("Document not found"));

        User signer = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Signature signature = Signature.builder()
                .document(document)
                .signer(signer)
                .xCoordinate(request.xCoordinate())
                .yCoordinate(request.yCoordinate())
                .pageNumber(request.pageNumber() == null ? 1 : request.pageNumber())
                .signedAt(LocalDateTime.now())
                .build();

        document.setStatus("SIGNED");
        documentRepository.save(document);

        Signature savedSignature =
        signatureRepository.save(signature);


        auditLogService.logAction(
              "SIGNATURE_SAVED",
               signer,
               document
            );


        return savedSignature;
    }

    public Signature getSignature(Long signatureId) {
        return signatureRepository.findById(signatureId)
                .orElseThrow(() -> new RuntimeException("Signature not found"));
    }

    public List<Signature> getSignaturesByDocument(Long documentId) {
        return signatureRepository.findByDocumentId(documentId);
    }

    public List<Signature> getSignaturesByUser(Long userId) {
        return signatureRepository.findBySignerId(userId);
    }

    public String finalizeSignature(Long signatureId) throws IOException {
        Signature signature = signatureRepository.findById(signatureId)
                .orElseThrow(() -> new RuntimeException("Signature not found"));

        Document document = signature.getDocument();
        User signer = signature.getSigner();

        String signedFile = pdfService.addSignature(
                document.getFilePath(),
                signer.getEmail(),
                signature.getXCoordinate().floatValue(),
                signature.getYCoordinate().floatValue(),
                signature.getPageNumber()
        );

        document.setFilePath(signedFile);
        document.setStatus("FINALIZED");
        documentRepository.save(document);

        auditLogService.logAction(
            "PDF_GENERATED",
             signature.getSigner(),
             signature.getDocument()
            );

        return signedFile;
    }
}
