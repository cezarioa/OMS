package com.example.OrderManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// 1. Add @JsonTypeInfo to define how type information is included (as a property called "@class")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
// 2. Add @JsonSubTypes to map the known concrete classes (optional but good practice)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Product.class),
        @JsonSubTypes.Type(value = Service.class)
})
public abstract class SellableItem implements Identifiable {

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