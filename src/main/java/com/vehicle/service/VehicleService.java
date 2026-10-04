package com.vehicle.service;

import com.vehicle.model.Insurance;
import com.vehicle.model.Owner;
import com.vehicle.model.Vehicle;
import com.vehicle.model.Workshop;
import com.vehicle.repository.OwnerRepository;
import com.vehicle.repository.VehicleRepository;
import com.vehicle.repository.WorkshopRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final WorkshopRepository workshopRepository;
    private final ActivityLogService activityLogService;

    public VehicleService(VehicleRepository vehicleRepository,
                          OwnerRepository ownerRepository,
                          WorkshopRepository workshopRepository,
                          ActivityLogService activityLogService) {
        this.vehicleRepository = vehicleRepository;
        this.ownerRepository = ownerRepository;
        this.workshopRepository = workshopRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public Vehicle registerVehicle(Long ownerId, Vehicle vehicleData) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));

        vehicleData.setOwner(owner);
        Vehicle saved = vehicleRepository.save(vehicleData);
        activityLogService.logActivity("Vehicle Registered: " + saved.getRegNo() + " | Model: " + saved.getModel() + " | Owner: " + owner.getName());
        return saved;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByRegNo(String regNo) {
        return vehicleRepository.findByRegNoIgnoreCase(regNo.trim())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with Reg No: " + regNo));
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + id));
    }

    @Transactional
    public Vehicle addInsurance(Long vehicleId, Insurance insurance) {
        Vehicle vehicle = getVehicleById(vehicleId);
        insurance.setVehicle(vehicle);
        vehicle.setInsurance(insurance);
        Vehicle saved = vehicleRepository.save(vehicle);
        activityLogService.logActivity("Insurance Added for Vehicle: " + saved.getRegNo() + " | Policy: " + insurance.getPolicyNo());
        return saved;
    }

    @Transactional
    public Vehicle addServiceRecord(Long vehicleId, Workshop workshop) {
        Vehicle vehicle = getVehicleById(vehicleId);
        workshop.setVehicle(vehicle);
        vehicle.getServiceHistory().add(workshop);
        Vehicle saved = vehicleRepository.save(vehicle);
        activityLogService.logActivity("Service Record Added for Vehicle: " + saved.getRegNo() + " | Workshop: " + workshop.getName());
        return saved;
    }

    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);
        activityLogService.logActivity("Vehicle Deleted: " + vehicle.getRegNo());
        vehicleRepository.delete(vehicle);
    }
}
