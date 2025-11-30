package com.example.OrderManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

// Store type information for JSON serialization
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Product.class),
        @JsonSubTypes.Type(value = Service.class)
})
@Entity
@Table(name = "sellable_items")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
