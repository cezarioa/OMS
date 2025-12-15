package com.example.OrderManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "contract_lines")
public class ContractLine implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellable_item_id")
    @NotNull(message = "Item must be selected.")
    private SellableItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    @NotNull(message = "Unit must be selected.")
    private UnitOfMeasure unit;

    @Column(nullable = false)
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must be greater than zero.")
    @Digits(integer = 6, fraction = 0, message = "Quantity must be a whole number.")
    private Double quantity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public SellableItem getItem() { return item; }
    public void setItem(SellableItem item) { this.item = item; }
    public UnitOfMeasure getUnit() { return unit; }
    public void setUnit(UnitOfMeasure unit) { this.unit = unit; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    @JsonIgnore
    @Transient
    public double getTotalValue() {
        if (item == null || quantity == null) return 0.0;
        return item.getUnitValue() * quantity;
    }
}
