package com.vehicle.service;

import com.vehicle.model.Owner;
import com.vehicle.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final ActivityLogService activityLogService;

    public OwnerService(OwnerRepository ownerRepository, ActivityLogService activityLogService) {
        this.ownerRepository = ownerRepository;
        this.activityLogService = activityLogService;
    }

    public Owner registerOwner(Owner owner) {
        Owner saved = ownerRepository.save(owner);
        activityLogService.logActivity("Owner Registered: " + saved.getName() + " (ID: " + saved.getId() + ")");
        return saved;
    }

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + id));
    }

    public Owner updateOwner(Long id, Owner ownerDetails) {
        Owner owner = getOwnerById(id);
        owner.setName(ownerDetails.getName());
        owner.setAddress(ownerDetails.getAddress());
        owner.setContact(ownerDetails.getContact());
        Owner updated = ownerRepository.save(owner);
        activityLogService.logActivity("Owner Updated: " + updated.getName() + " (ID: " + updated.getId() + ")");
        return updated;
    }

    public void deleteOwner(Long id) {
        Owner owner = getOwnerById(id);
        ownerRepository.delete(owner);
        activityLogService.logActivity("Owner Deleted: " + owner.getName() + " (ID: " + id + ")");
    }
}
