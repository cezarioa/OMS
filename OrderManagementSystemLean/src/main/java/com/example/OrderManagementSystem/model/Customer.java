package com.example.OrderManagementSystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Currency is required.")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code.")
    @Column(length = 3, nullable = false)
    private String currency;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    @Column
    private String email;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Order> orders = new ArrayList<>();

    @Transient
    private List<Contract> contracts = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    public List<Contract> getContracts() { return contracts; }
    public void setContracts(List<Contract> contracts) { this.contracts = contracts; }

    public void addOrder(Order order){
        orders.add(order);
        order.setCustomer(this);
    }
    public void addContract(Contract contract){ contracts.add(contract); }
}
