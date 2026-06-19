package com.kovid.doc_signature.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kovid.doc_signature.model.Document;
import com.kovid.doc_signature.model.SigningToken;
import com.kovid.doc_signature.repository.DocumentRepository;
import com.kovid.doc_signature.repository.SigningTokenRepository;

@Service
public class SigningLinkService {

    private final SigningTokenRepository signingTokenRepository;
    private final DocumentRepository documentRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public SigningLinkService(
            SigningTokenRepository signingTokenRepository,
            DocumentRepository documentRepository,
            EmailService emailService

        ){
        this.signingTokenRepository = signingTokenRepository;
        this.documentRepository = documentRepository;
        this.emailService = emailService;
    }

    public String generateSigningLink(Long documentId, String signerEmail){

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        String token = UUID.randomUUID().toString();

        SigningToken signingToken = SigningToken.builder()
                .token(token)
                .signerEmail(signerEmail)
                .document(document)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .used(false)
                .build();

        signingTokenRepository.save(signingToken);

       String link = frontendUrl + "/sign/" + token;

emailService.sendSigningEmail(
        signerEmail,
        link
);

return link;
    }
}
