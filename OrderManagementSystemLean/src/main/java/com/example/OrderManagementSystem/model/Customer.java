package com.example.OrderManagementSystem.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private Long id;
    private String name;
    private String currency;
    private List<Order> orders = new ArrayList<>();
    private List<Contract> contracts = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    public List<Contract> getContracts() { return contracts; }
    public void setContracts(List<Contract> contracts) { this.contracts = contracts; }
    public void addOrder(Order o){ orders.add(o); }
    public void addContract(Contract c){ contracts.add(c); }
}
