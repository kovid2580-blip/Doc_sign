package com.kovid.doc_signature.dto;

import java.time.LocalDateTime;

import com.kovid.doc_signature.model.Signature;

public record SignatureResponse(
    Long id,
    Long documentId,
    String documentFileName,
    Long signerId,
    String signerEmail,
    Double xCoordinate,
    Double yCoordinate,
    Integer pageNumber,
    LocalDateTime signedAt
) {
    public static SignatureResponse from(Signature signature) {
        return new SignatureResponse(
            signature.getId(),
            signature.getDocument().getId(),
            signature.getDocument().getFileName(),
            signature.getSigner().getId(),
            signature.getSigner().getEmail(),
            signature.getXCoordinate(),
            signature.getYCoordinate(),
            signature.getPageNumber(),
            signature.getSignedAt()
        );
    }
}
