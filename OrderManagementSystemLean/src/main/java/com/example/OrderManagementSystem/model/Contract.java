package com.example.OrderManagementSystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contracts")
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Contract name is required.")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.ACTIVE;

    @NotNull(message = "Contract type selection is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractType contractType;

    @OneToMany(
            mappedBy = "contract",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Valid
    private List<ContractLine> contractLines = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public List<ContractLine> getContractLines() { return contractLines; }
    public void setContractLines(List<ContractLine> contractLines) { this.contractLines = contractLines; }
    public void addContractLine(ContractLine line){
        contractLines.add(line);
        line.setContract(this);
    }

    public void activate(){ this.status = ContractStatus.ACTIVE; }
    public void deactivate(){ this.status = ContractStatus.DOWN; }
}
