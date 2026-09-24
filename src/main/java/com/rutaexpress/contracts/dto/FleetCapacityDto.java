package com.rutaexpress.contracts.dto;

import com.rutaexpress.contracts.FleetStatus;

/**
 * Vehículo de la flota con su capacidad máxima, disponible y estado.
 */
public record FleetCapacityDto(
        Long id,
        String vehicleType,
        double maxWeightKg,
        double maxVolumeM3,
        double availableWeightKg,
        double availableVolumeM3,
        FleetStatus status) {
}
