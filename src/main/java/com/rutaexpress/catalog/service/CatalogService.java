package com.rutaexpress.catalog.service;

import com.rutaexpress.contracts.FleetStatus;
import com.rutaexpress.contracts.dto.FleetCapacityDto;
import com.rutaexpress.contracts.dto.FleetCapacityRequest;
import com.rutaexpress.contracts.dto.ServiceTypeDto;
import com.rutaexpress.contracts.dto.ServiceTypeRequest;
import com.rutaexpress.catalog.domain.FleetCapacity;
import com.rutaexpress.catalog.domain.FleetCapacityRepository;
import com.rutaexpress.catalog.domain.ServiceType;
import com.rutaexpress.catalog.domain.ServiceTypeRepository;
import com.rutaexpress.catalog.exception.InsufficientCapacityException;
import com.rutaexpress.catalog.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private static final double EPS = 1e-9;

    private final ServiceTypeRepository serviceTypes;
    private final FleetCapacityRepository fleet;

    public CatalogService(ServiceTypeRepository serviceTypes, FleetCapacityRepository fleet) {
        this.serviceTypes = serviceTypes;
        this.fleet = fleet;
    }

    @Transactional(readOnly = true)
    public List<ServiceTypeDto> listServices() {
        return serviceTypes.findAll().stream().map(this::toServiceTypeDto).toList();
    }

    @Transactional
    public ServiceTypeDto createService(ServiceTypeRequest request) {
        if (request == null || isBlank(request.name())) {
            throw new IllegalArgumentException("name es obligatorio");
        }
        ServiceType entity = new ServiceType();
        entity.setName(request.name());
        entity.setBasePrice(request.basePrice());
        entity.setPricePerKm(request.pricePerKm());
        entity.setPricePerKg(request.pricePerKg());
        entity.setEstimatedHours(request.estimatedHours());
        return toServiceTypeDto(serviceTypes.save(entity));
    }

    @Transactional(readOnly = true)
    public ServiceTypeDto getService(Long id) {
        return toServiceTypeDto(loadService(id));
    }

    @Transactional
    public ServiceTypeDto updateService(Long id, ServiceTypeRequest request) {
        if (request == null || isBlank(request.name())) {
            throw new IllegalArgumentException("name es obligatorio");
        }
        ServiceType entity = loadService(id);
        entity.setName(request.name());
        entity.setBasePrice(request.basePrice());
        entity.setPricePerKm(request.pricePerKm());
        entity.setPricePerKg(request.pricePerKg());
        entity.setEstimatedHours(request.estimatedHours());
        return toServiceTypeDto(serviceTypes.save(entity));
    }

    @Transactional(readOnly = true)
    public List<FleetCapacityDto> listFleet() {
        return fleet.findAll().stream().map(this::toFleetDto).toList();
    }

    @Transactional
    public FleetCapacityDto createFleet(FleetCapacityRequest request) {
        if (request == null || isBlank(request.vehicleType())) {
            throw new IllegalArgumentException("vehicleType es obligatorio");
        }
        FleetCapacity entity = new FleetCapacity();
        entity.setVehicleType(request.vehicleType());
        entity.setMaxWeightKg(request.maxWeightKg());
        entity.setMaxVolumeM3(request.maxVolumeM3());
        entity.setAvailableWeightKg(request.maxWeightKg());
        entity.setAvailableVolumeM3(request.maxVolumeM3());
        entity.setStatus(FleetStatus.AVAILABLE);
        return toFleetDto(fleet.save(entity));
    }

    @Transactional
    public FleetCapacityDto reserve(Long id, double weightKg, double volumeM3) {
        if (weightKg <= 0 || volumeM3 <= 0) {
            throw new IllegalArgumentException("weightKg y volumeM3 deben ser positivos");
        }
        FleetCapacity entity = loadFleet(id);
        if (entity.getStatus() != FleetStatus.AVAILABLE) {
            throw new InsufficientCapacityException(
                    "Vehículo no disponible para reserva (estado: " + entity.getStatus() + ")");
        }
        if (entity.getAvailableWeightKg() < weightKg || entity.getAvailableVolumeM3() < volumeM3) {
            throw new InsufficientCapacityException("Capacidad insuficiente en el vehículo " + id);
        }
        entity.setAvailableWeightKg(clampZero(entity.getAvailableWeightKg() - weightKg));
        entity.setAvailableVolumeM3(clampZero(entity.getAvailableVolumeM3() - volumeM3));
        if (entity.getAvailableWeightKg() == 0 || entity.getAvailableVolumeM3() == 0) {
            entity.setStatus(FleetStatus.BUSY);
        }
        return toFleetDto(fleet.save(entity));
    }

    @Transactional
    public FleetCapacityDto updateFleetStatus(Long id, FleetStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status es obligatorio");
        }
        FleetCapacity entity = loadFleet(id);
        entity.setStatus(status);
        return toFleetDto(fleet.save(entity));
    }

    private ServiceType loadService(Long id) {
        return serviceTypes.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceType", id));
    }

    private FleetCapacity loadFleet(Long id) {
        return fleet.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FleetCapacity", id));
    }

    private ServiceTypeDto toServiceTypeDto(ServiceType entity) {
        return new ServiceTypeDto(entity.getId(), entity.getName(), entity.getBasePrice(),
                entity.getPricePerKm(), entity.getPricePerKg(), entity.getEstimatedHours());
    }

    private FleetCapacityDto toFleetDto(FleetCapacity entity) {
        return new FleetCapacityDto(entity.getId(), entity.getVehicleType(),
                entity.getMaxWeightKg(), entity.getMaxVolumeM3(),
                entity.getAvailableWeightKg(), entity.getAvailableVolumeM3(), entity.getStatus());
    }

    private double clampZero(double value) {
        return value <= EPS ? 0 : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
