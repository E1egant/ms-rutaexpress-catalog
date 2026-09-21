package com.rutaexpress.contracts.dto;

import com.rutaexpress.contracts.FleetStatus;

/**
 * Vehículo de la flota con su capacidad y estado.
 */
public record FleetCapacityDto(
        Long id,
        String vehicleType,
        double maxWeightKg,
        double maxVolumeM3,
        FleetStatus status) {
}
