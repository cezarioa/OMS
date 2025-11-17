package com.example.OrderManagementSystem.model;

public class UnitOfMeasure implements Identifiable {
    private Long id;
    private String name;
    private String symbol;

    public UnitOfMeasure() {}
    public UnitOfMeasure(Long id, String name, String symbol) {
        this.id = id; this.name = name; this.symbol = symbol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}