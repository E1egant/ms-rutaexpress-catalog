package com.rutaexpress.contracts.dto;

/**
 * Payload de creación de un vehículo de flota.
 */
public record FleetCapacityRequest(
        String vehicleType,
        double maxWeightKg,
        double maxVolumeM3) {
}
