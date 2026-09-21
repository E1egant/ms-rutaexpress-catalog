package com.rutaexpress.contracts.dto;

import java.math.BigDecimal;

/**
 * Tipo de servicio de envío con su tarifa.
 */
public record ServiceTypeDto(
        Long id,
        String name,
        BigDecimal basePrice,
        BigDecimal pricePerKm,
        BigDecimal pricePerKg,
        int estimatedHours) {
}
