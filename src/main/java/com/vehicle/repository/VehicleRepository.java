package com.vehicle.repository;

import com.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByRegNo(String regNo);
    Optional<Vehicle> findByRegNoIgnoreCase(String regNo);
}
