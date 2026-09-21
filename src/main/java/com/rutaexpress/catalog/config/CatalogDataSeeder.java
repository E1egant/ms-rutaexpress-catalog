package com.rutaexpress.catalog.config;

import com.rutaexpress.catalog.domain.FleetCapacity;
import com.rutaexpress.catalog.domain.FleetCapacityRepository;
import com.rutaexpress.catalog.domain.ServiceType;
import com.rutaexpress.catalog.domain.ServiceTypeRepository;
import com.rutaexpress.contracts.FleetStatus;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CatalogDataSeeder implements CommandLineRunner {

    private final ServiceTypeRepository serviceTypes;
    private final FleetCapacityRepository fleet;

    public CatalogDataSeeder(ServiceTypeRepository serviceTypes, FleetCapacityRepository fleet) {
        this.serviceTypes = serviceTypes;
        this.fleet = fleet;
    }

    @Override
    public void run(String... args) {
        if (serviceTypes.count() == 0) {
            serviceTypes.saveAll(List.of(
                    serviceType("Estándar", "10.00", "0.50", "0.80", 48),
                    serviceType("Express", "15.00", "0.75", "1.00", 24),
                    serviceType("Mismo día", "25.00", "1.00", "1.50", 8)));
        }
        if (fleet.count() == 0) {
            fleet.saveAll(List.of(
                    fleet("Moto", 30, 0.4, FleetStatus.AVAILABLE),
                    fleet("Van", 1200, 12, FleetStatus.AVAILABLE),
                    fleet("Camión", 8000, 60, FleetStatus.MAINTENANCE)));
        }
    }

    private ServiceType serviceType(String name, String base, String perKm, String perKg, int hours) {
        ServiceType serviceType = new ServiceType();
        serviceType.setName(name);
        serviceType.setBasePrice(new BigDecimal(base));
        serviceType.setPricePerKm(new BigDecimal(perKm));
        serviceType.setPricePerKg(new BigDecimal(perKg));
        serviceType.setEstimatedHours(hours);
        return serviceType;
    }

    private FleetCapacity fleet(String type, double weightKg, double volumeM3, FleetStatus status) {
        FleetCapacity fleet = new FleetCapacity();
        fleet.setVehicleType(type);
        fleet.setMaxWeightKg(weightKg);
        fleet.setMaxVolumeM3(volumeM3);
        fleet.setStatus(status);
        return fleet;
    }
}
