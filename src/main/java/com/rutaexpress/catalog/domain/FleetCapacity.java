package com.rutaexpress.catalog.domain;

import com.rutaexpress.contracts.FleetStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fleet_capacity")
public class FleetCapacity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private double maxWeightKg;

    @Column(nullable = false)
    private double maxVolumeM3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FleetStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getMaxWeightKg() {
        return maxWeightKg;
    }

    public void setMaxWeightKg(double maxWeightKg) {
        this.maxWeightKg = maxWeightKg;
    }

    public double getMaxVolumeM3() {
        return maxVolumeM3;
    }

    public void setMaxVolumeM3(double maxVolumeM3) {
        this.maxVolumeM3 = maxVolumeM3;
    }

    public FleetStatus getStatus() {
        return status;
    }

    public void setStatus(FleetStatus status) {
        this.status = status;
    }
}
