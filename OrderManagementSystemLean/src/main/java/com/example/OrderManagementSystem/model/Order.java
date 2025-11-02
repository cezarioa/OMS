package com.example.OrderManagementSystem.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private String name;
    private Customer customer;
    private Contract contract; // optional
    private List<OrderLine> orderLines = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }
    public List<OrderLine> getOrderLines() { return orderLines; }
    public void setOrderLines(List<OrderLine> orderLines) { this.orderLines = orderLines; }
    public void addOrderLine(OrderLine line){ orderLines.add(line); }
}
