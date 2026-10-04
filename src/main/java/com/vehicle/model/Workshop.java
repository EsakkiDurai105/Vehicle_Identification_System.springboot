package com.vehicle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workshops")
public class Workshop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Workshop name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @ElementCollection
    @CollectionTable(name = "workshop_services", joinColumns = @JoinColumn(name = "workshop_id"))
    @Column(name = "service")
    private List<String> services = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonBackReference("vehicle-workshops")
    private Vehicle vehicle;

    public Workshop() {}

    public Workshop(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
