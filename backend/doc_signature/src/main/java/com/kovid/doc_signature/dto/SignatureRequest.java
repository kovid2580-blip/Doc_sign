package com.kovid.doc_signature.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SignatureRequest(
    @NotNull Long documentId,
    @NotNull Long userId,
    @NotNull @PositiveOrZero Double xCoordinate,
    @NotNull @PositiveOrZero Double yCoordinate,
    @Positive Integer pageNumber
) {
}
