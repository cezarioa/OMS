package com.example.OrderManagementSystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRODUCT")
public class Product extends SellableItem {
    @Column(name = "unit_value")
    private double value;

    @Column(length = 1024)
    private String description;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
