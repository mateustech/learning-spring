package main.customer.presentation.rest.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
    String message,
    OffsetDateTime timestamp
) {

    public static ApiErrorResponse of(String message) {
        return new ApiErrorResponse(message, OffsetDateTime.now());
    }
}
