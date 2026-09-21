package com.rutaexpress.contracts.dto;

import java.math.BigDecimal;

/**
 * Payload de creación de un tipo de servicio de envío.
 */
public record ServiceTypeRequest(
        String name,
        BigDecimal basePrice,
        BigDecimal pricePerKm,
        BigDecimal pricePerKg,
        int estimatedHours) {
}
