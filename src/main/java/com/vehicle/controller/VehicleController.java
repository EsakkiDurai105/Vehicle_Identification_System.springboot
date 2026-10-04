package com.vehicle.controller;

import com.vehicle.model.Insurance;
import com.vehicle.model.Vehicle;
import com.vehicle.model.Workshop;
import com.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/owner/{ownerId}")
    public ResponseEntity<Vehicle> registerVehicle(@PathVariable Long ownerId,
                                                    @Valid @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.registerVehicle(ownerId, vehicle));
    }

    @GetMapping
    public ResponseEntity<?> getAllVehicles(jakarta.servlet.http.HttpSession session) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        if ("USER".equals(session.getAttribute("role"))) {
            return ResponseEntity.ok(vehicles.stream().map(vehicle -> Map.of(
                    "id", vehicle.getId(),
                    "regNo", vehicle.getRegNo(),
                    "model", vehicle.getModel(),
                    "mileage", vehicle.getMileage(),
                    "insured", vehicle.getInsurance() != null,
                    "serviceCount", vehicle.getServiceHistory().size()
            )).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search/{regNo}")
    public ResponseEntity<Vehicle> searchByRegNo(@PathVariable String regNo) {
        return ResponseEntity.ok(vehicleService.getVehicleByRegNo(regNo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PostMapping("/{vehicleId}/insurance")
    public ResponseEntity<Vehicle> addInsurance(@PathVariable Long vehicleId,
                                                 @Valid @RequestBody Insurance insurance) {
        return ResponseEntity.ok(vehicleService.addInsurance(vehicleId, insurance));
    }

    @PostMapping("/{vehicleId}/service")
    public ResponseEntity<Vehicle> addServiceRecord(@PathVariable Long vehicleId,
                                                     @Valid @RequestBody Workshop workshop) {
        return ResponseEntity.ok(vehicleService.addServiceRecord(vehicleId, workshop));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<java.util.Map<String, String>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Vehicle deleted successfully"));
    }
}
