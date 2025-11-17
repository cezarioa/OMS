package com.example.OrderManagementSystem.model;

// Note: We use the static nested class reference for the enum type
import com.example.OrderManagementSystem.model.SellableItem.SellableItemStatus;

public class Service extends SellableItem {
    // The field itself stays here, as per the UML design.
    private SellableItemStatus status;

    public SellableItemStatus getStatus() {
        return status;
    }

    public void setStatus(SellableItemStatus status) {
        this.status = status;
    }
}