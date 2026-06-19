package com.kovid.doc_signature.dto;

public record LoginResponse(
    String message,
    String token,
    String tokenType,
    Long userId,
    String email,
    long expiresIn
) {
}
