package com.example.OrderManagementSystem.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRODUCT")
public class Product extends SellableItem {
}
