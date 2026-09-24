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
import com.rutaexpress.catalog.exception.InsufficientCapacityException;
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

    @Test
    void updateServiceActualizaLaTarifa() {
        ServiceType entity = new ServiceType();
        entity.setName("Viejo");
        when(serviceTypes.findById(1L)).thenReturn(Optional.of(entity));
        when(serviceTypes.save(entity)).thenReturn(entity);

        ServiceTypeDto dto = service.updateService(1L, new ServiceTypeRequest(
                "Nuevo", new BigDecimal("2000"), new BigDecimal("20"), new BigDecimal("10"), 12));

        assertThat(dto.name()).isEqualTo("Nuevo");
        assertThat(dto.basePrice()).isEqualByComparingTo("2000");
    }

    @Test
    void updateServiceInexistenteLanzaNotFound() {
        when(serviceTypes.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateService(99L, new ServiceTypeRequest(
                        "X", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createFleetInicializaDisponibleAlMaximo() {
        when(fleet.save(any(FleetCapacity.class))).thenAnswer(inv -> inv.getArgument(0));

        FleetCapacityDto dto = service.createFleet(new FleetCapacityRequest("Van", 100.0, 10.0));

        assertThat(dto.availableWeightKg()).isEqualTo(100.0);
        assertThat(dto.availableVolumeM3()).isEqualTo(10.0);
        assertThat(dto.status()).isEqualTo(FleetStatus.AVAILABLE);
    }

    @Test
    void reserveDescuentaDisponible() {
        FleetCapacity entity = fleet(100.0, 10.0, FleetStatus.AVAILABLE);
        when(fleet.findById(1L)).thenReturn(Optional.of(entity));
        when(fleet.save(entity)).thenReturn(entity);

        FleetCapacityDto dto = service.reserve(1L, 30.0, 4.0);

        assertThat(dto.availableWeightKg()).isEqualTo(70.0);
        assertThat(dto.availableVolumeM3()).isEqualTo(6.0);
        assertThat(dto.status()).isEqualTo(FleetStatus.AVAILABLE);
    }

    @Test
    void reserveAgotaYLlevaABusy() {
        FleetCapacity entity = fleet(10.0, 5.0, FleetStatus.AVAILABLE);
        when(fleet.findById(1L)).thenReturn(Optional.of(entity));
        when(fleet.save(entity)).thenReturn(entity);

        FleetCapacityDto dto = service.reserve(1L, 10.0, 5.0);

        assertThat(dto.availableWeightKg()).isZero();
        assertThat(dto.status()).isEqualTo(FleetStatus.BUSY);
    }

    @Test
    void reserveSinCapacidadLanzaConflict() {
        FleetCapacity entity = fleet(5.0, 1.0, FleetStatus.AVAILABLE);
        when(fleet.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.reserve(1L, 10.0, 1.0))
                .isInstanceOf(InsufficientCapacityException.class);
    }

    @Test
    void reserveEnVehiculoNoDisponibleLanzaConflict() {
        FleetCapacity entity = fleet(100.0, 10.0, FleetStatus.MAINTENANCE);
        when(fleet.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.reserve(1L, 1.0, 0.1))
                .isInstanceOf(InsufficientCapacityException.class);
    }

    @Test
    void reserveConValoresNoPositivosLanzaIllegalArgument() {
        assertThatThrownBy(() -> service.reserve(1L, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private FleetCapacity fleet(double maxWeightKg, double maxVolumeM3, FleetStatus status) {
        FleetCapacity entity = new FleetCapacity();
        entity.setVehicleType("Van");
        entity.setMaxWeightKg(maxWeightKg);
        entity.setMaxVolumeM3(maxVolumeM3);
        entity.setAvailableWeightKg(maxWeightKg);
        entity.setAvailableVolumeM3(maxVolumeM3);
        entity.setStatus(status);
        return entity;
    }
}
