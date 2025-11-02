package com.example.OrderManagementSystem.model;

public class ContractLine {
    private Long id;
    private SellableItem item;
    private UnitOfMeasure unit;
    private double quantity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SellableItem getItem() { return item; }
    public void setItem(SellableItem item) { this.item = item; }
    public UnitOfMeasure getUnit() { return unit; }
    public void setUnit(UnitOfMeasure unit) { this.unit = unit; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getTotalValue() {
        if (item instanceof Product p) return p.getValue() * quantity;
        return 0.0;
    }
}
