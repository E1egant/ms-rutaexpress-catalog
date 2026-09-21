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
import com.rutaexpress.catalog.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

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
        entity.setStatus(FleetStatus.AVAILABLE);
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
                entity.getMaxWeightKg(), entity.getMaxVolumeM3(), entity.getStatus());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
