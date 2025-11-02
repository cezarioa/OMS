package com.example.OrderManagementSystem.model;

public class Service extends SellableItem {
    private String status; // Active, Down
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
