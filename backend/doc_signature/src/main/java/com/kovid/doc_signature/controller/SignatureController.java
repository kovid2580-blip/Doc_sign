package com.kovid.doc_signature.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kovid.doc_signature.dto.SignatureRequest;
import com.kovid.doc_signature.dto.SignatureResponse;
import com.kovid.doc_signature.model.Signature;
import com.kovid.doc_signature.service.SignatureService;

@RestController
@RequestMapping("/api/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping
    public ResponseEntity<SignatureResponse> saveSignature(
            @Valid @RequestBody SignatureRequest request
    ) {
        Signature signature = signatureService.saveSignature(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(SignatureResponse.from(signature));
    }

    @GetMapping("/{signatureId}")
    public ResponseEntity<SignatureResponse> getSignature(
            @PathVariable Long signatureId
    ) {
        Signature signature = signatureService.getSignature(signatureId);

        return ResponseEntity.ok(SignatureResponse.from(signature));
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<SignatureResponse>> getSignaturesByDocument(
            @PathVariable Long documentId
    ) {
        List<SignatureResponse> signatures = signatureService.getSignaturesByDocument(documentId)
                .stream()
                .map(SignatureResponse::from)
                .toList();

        return ResponseEntity.ok(signatures);
    }

    @GetMapping
    public ResponseEntity<List<SignatureResponse>> getSignaturesByUser(
            @RequestParam("userId") Long userId
    ) {
        List<SignatureResponse> signatures = signatureService.getSignaturesByUser(userId)
                .stream()
                .map(SignatureResponse::from)
                .toList();

        return ResponseEntity.ok(signatures);
    }

    @PostMapping("/finalize/{signatureId}")
    public ResponseEntity<String> finalizeSignature(
            @PathVariable Long signatureId
    ) throws Exception {
        String signedFile = signatureService.finalizeSignature(signatureId);
        return ResponseEntity.ok("Signed PDF created: " + signedFile);
    }
}
