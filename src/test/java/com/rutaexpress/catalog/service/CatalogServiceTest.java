package com.rutaexpress.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    ServiceTypeRepository serviceTypes;
    @Mock
    FleetCapacityRepository fleet;
    @InjectMocks
    CatalogService service;

    @Test
    void createServiceGuardaYDevuelveElDto() {
        when(serviceTypes.save(any(ServiceType.class))).thenAnswer(inv -> inv.getArgument(0));

        ServiceTypeDto dto = service.createService(new ServiceTypeRequest(
                "Express", new BigDecimal("1000"), new BigDecimal("10"), new BigDecimal("5"), 24));

        assertThat(dto.name()).isEqualTo("Express");
        assertThat(dto.basePrice()).isEqualByComparingTo("1000");
        assertThat(dto.estimatedHours()).isEqualTo(24);
    }

    @Test
    void createServiceSinNombreLanzaIllegalArgument() {
        assertThatThrownBy(() -> service.createService(new ServiceTypeRequest(
                " ", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getServiceInexistenteLanzaNotFound() {
        when(serviceTypes.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getService(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listServicesMapeaLasEntidades() {
        ServiceType entity = new ServiceType();
        entity.setName("Standard");
        entity.setBasePrice(BigDecimal.TEN);
        entity.setPricePerKm(BigDecimal.ONE);
        entity.setPricePerKg(BigDecimal.ONE);
        entity.setEstimatedHours(48);
        when(serviceTypes.findAll()).thenReturn(List.of(entity));

        assertThat(service.listServices()).extracting(ServiceTypeDto::name).containsExactly("Standard");
    }

    @Test
    void createFleetQuedaDisponible() {
        when(fleet.save(any(FleetCapacity.class))).thenAnswer(inv -> inv.getArgument(0));

        FleetCapacityDto dto = service.createFleet(new FleetCapacityRequest("Moto", 20.0, 0.2));

        assertThat(dto.vehicleType()).isEqualTo("Moto");
        assertThat(dto.status()).isEqualTo(FleetStatus.AVAILABLE);
    }

    @Test
    void createFleetSinTipoLanzaIllegalArgument() {
        assertThatThrownBy(() -> service.createFleet(new FleetCapacityRequest("", 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateFleetStatusCambiaElEstado() {
        FleetCapacity entity = new FleetCapacity();
        entity.setVehicleType("Van");
        entity.setStatus(FleetStatus.AVAILABLE);
        when(fleet.findById(1L)).thenReturn(Optional.of(entity));
        when(fleet.save(entity)).thenReturn(entity);

        FleetCapacityDto dto = service.updateFleetStatus(1L, FleetStatus.BUSY);

        assertThat(dto.status()).isEqualTo(FleetStatus.BUSY);
        verify(fleet).save(entity);
    }

    @Test
    void updateFleetStatusNuloLanzaIllegalArgument() {
        assertThatThrownBy(() -> service.updateFleetStatus(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
