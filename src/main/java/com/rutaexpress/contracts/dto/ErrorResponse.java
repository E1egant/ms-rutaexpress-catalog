package com.rutaexpress.contracts.dto;

import java.time.Instant;

/**
 * Cuerpo de error estándar para las respuestas de error de las APIs.
 */
public record ErrorResponse(int status, String message, Instant timestamp) {
}
