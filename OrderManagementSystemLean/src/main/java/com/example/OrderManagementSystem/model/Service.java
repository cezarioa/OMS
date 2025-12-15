package com.example.OrderManagementSystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SERVICE")
public class Service extends SellableItem {
    @Column(length = 512)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDetails() {
        return description != null ? description : "";
    }
}
