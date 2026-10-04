package com.vehicle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String regNo;

    @NotBlank(message = "Model is required")
    private String model;

    @Positive(message = "Mileage must be positive")
    private double mileage;

    @Positive(message = "Cost must be positive")
    private double cost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties("vehicles")
    private Owner owner;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "insurance_id")
    @JsonManagedReference("vehicle-insurance")
    private Insurance insurance;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("vehicle-workshops")
    private List<Workshop> serviceHistory = new ArrayList<>();

    public Vehicle() {}

    public Vehicle(String model, double mileage, double cost, Owner owner) {
        this.model = model;
        this.mileage = mileage;
        this.cost = cost;
        this.owner = owner;
    }

    @PrePersist
    public void generateRegNo() {
        if (this.regNo == null || this.regNo.isEmpty()) {
            this.regNo = "VEH" + System.currentTimeMillis() % 100000;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }
    public Insurance getInsurance() { return insurance; }
    public void setInsurance(Insurance insurance) { this.insurance = insurance; }
    public List<Workshop> getServiceHistory() { return serviceHistory; }
    public void setServiceHistory(List<Workshop> serviceHistory) { this.serviceHistory = serviceHistory; }
}
