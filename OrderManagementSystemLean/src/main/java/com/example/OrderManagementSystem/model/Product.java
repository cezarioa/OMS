package com.example.OrderManagementSystem.model;

public class Product extends SellableItem {
    private double value;
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
