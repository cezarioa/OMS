package com.example.OrderManagementSystem.model;

import java.util.ArrayList;
import java.util.List;

public class Contract implements Identifiable {

    /**
     * Represents the possible statuses for a Contract.
     * Defined as an inner enum, as it's tightly coupled to the Contract.
     */
    public enum ContractStatus {
        ACTIVE("Active"),
        DOWN("Down");

        private final String displayName;

        ContractStatus(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Returns the display-friendly name of the status.
         * @return A string like "Active" or "Down".
         */
        @Override
        public String toString() {
            return displayName;
        }
    }

    private Long id;
    private String name;
    // Changed status from String to the ContractStatus enum
    private ContractStatus status = ContractStatus.ACTIVE;
    private Long contractTypeId;
    private List<ContractLine> contractLines = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Updated getter to return the enum
    public ContractStatus getStatus() { return status; }
    // Updated setter to accept the enum
    public void setStatus(ContractStatus status) { this.status = status; }

    public Long getContractTypeId() { return contractTypeId; }
    public void setContractTypeId(Long contractTypeId) { this.contractTypeId = contractTypeId; }
    public List<ContractLine> getContractLines() { return contractLines; }
    public void setContractLines(List<ContractLine> contractLines) { this.contractLines = contractLines; }
    public void addContractLine(ContractLine line){ contractLines.add(line); }

    // Updated methods to use the enum values
    public void activate(){ this.status = ContractStatus.ACTIVE; }
    public void deactivate(){ this.status = ContractStatus.DOWN; }
}