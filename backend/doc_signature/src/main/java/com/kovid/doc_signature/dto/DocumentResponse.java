package com.kovid.doc_signature.dto;

import java.time.LocalDateTime;

import com.kovid.doc_signature.model.Document;

public record DocumentResponse(
    Long id,
    String fileName,
    String filePath,
    LocalDateTime uploadedAt,
    String status,
    Long ownerId,
    String ownerEmail
) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
            document.getId(),
            document.getFileName(),
            document.getFilePath(),
            document.getUploadedAt(),
            document.getStatus(),
            document.getOwner().getId(),
            document.getOwner().getEmail()
        );
    }
}
