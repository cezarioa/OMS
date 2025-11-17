package com.example.OrderManagementSystem.model;

public abstract class SellableItem {

    /**
     * Defines the canonical status values for status-enabled subclasses
     * (like Service). Placing the definition here makes the type universally
     * available to all SellableItem extensions while respecting encapsulation.
     */
    public enum SellableItemStatus {
        ACTIVE,
        DOWN
    }

    private Long id;
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}