package com.api.materiais.dto.response;

import java.time.LocalDateTime;

public record StandardErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String origin,
        String path
) {}