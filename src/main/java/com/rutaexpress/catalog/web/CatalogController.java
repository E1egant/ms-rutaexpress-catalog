package com.rutaexpress.catalog.web;

import com.rutaexpress.contracts.ApiPaths;
import com.rutaexpress.contracts.dto.FleetCapacityDto;
import com.rutaexpress.contracts.dto.FleetCapacityRequest;
import com.rutaexpress.contracts.dto.ServiceTypeDto;
import com.rutaexpress.contracts.dto.ServiceTypeRequest;
import com.rutaexpress.catalog.service.CatalogService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatalogController {

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping(ApiPaths.SERVICES)
    public List<ServiceTypeDto> listServices() {
        return service.listServices();
    }

    @GetMapping(ApiPaths.SERVICES + "/{id}")
    public ServiceTypeDto getService(@PathVariable Long id) {
        return service.getService(id);
    }

    @PostMapping(ApiPaths.SERVICES)
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceTypeDto createService(@RequestBody ServiceTypeRequest request) {
        return service.createService(request);
    }

    @GetMapping(ApiPaths.FLEET)
    public List<FleetCapacityDto> listFleet() {
        return service.listFleet();
    }

    @PostMapping(ApiPaths.FLEET)
    @ResponseStatus(HttpStatus.CREATED)
    public FleetCapacityDto createFleet(@RequestBody FleetCapacityRequest request) {
        return service.createFleet(request);
    }

    @PatchMapping(ApiPaths.FLEET + "/{id}/status")
    public FleetCapacityDto updateFleetStatus(@PathVariable Long id, @RequestBody FleetStatusRequest body) {
        return service.updateFleetStatus(id, body.status());
    }
}
