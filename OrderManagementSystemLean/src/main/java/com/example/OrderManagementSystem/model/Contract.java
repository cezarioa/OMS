package com.example.OrderManagementSystem.model;

import java.util.ArrayList;
import java.util.List;

public class Contract {
    private Long id;
    private String name;
    private String status = "Active";
    private Long contractTypeId;
    private List<ContractLine> contractLines = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getContractTypeId() { return contractTypeId; }
    public void setContractTypeId(Long contractTypeId) { this.contractTypeId = contractTypeId; }
    public List<ContractLine> getContractLines() { return contractLines; }
    public void setContractLines(List<ContractLine> contractLines) { this.contractLines = contractLines; }
    public void addContractLine(ContractLine line){ contractLines.add(line); }
    public void activate(){ this.status = "Active"; }
    public void deactivate(){ this.status = "Down"; }
}
