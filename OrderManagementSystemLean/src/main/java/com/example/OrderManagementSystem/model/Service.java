package com.example.OrderManagementSystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

// Note: We use the static nested class reference for the enum type
import com.example.OrderManagementSystem.model.SellableItem.SellableItemStatus;

@Entity
@DiscriminatorValue("SERVICE")
public class Service extends SellableItem {
    @Enumerated(EnumType.STRING)
    @Column(name = "service_status")
    private SellableItemStatus status = SellableItemStatus.ACTIVE;

    public SellableItemStatus getStatus() {
        return status;
    }

    public void setStatus(SellableItemStatus status) {
        this.status = status;
    }
}
