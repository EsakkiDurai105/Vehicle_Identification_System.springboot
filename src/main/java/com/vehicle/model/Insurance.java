package com.vehicle.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "insurances")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Policy number is required")
    private String policyNo;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Validity is required")
    private String validity;

    @OneToOne(mappedBy = "insurance")
    @JsonBackReference("vehicle-insurance")
    private Vehicle vehicle;

    public Insurance() {}

    public Insurance(String policyNo, String company, String type, String validity) {
        this.policyNo = policyNo;
        this.company = company;
        this.type = type;
        this.validity = validity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPolicyNo() { return policyNo; }
    public void setPolicyNo(String policyNo) { this.policyNo = policyNo; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValidity() { return validity; }
    public void setValidity(String validity) { this.validity = validity; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
