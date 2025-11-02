package com.example.OrderManagementSystem.model;

public class Product extends SellableItem {
    private double value;
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
