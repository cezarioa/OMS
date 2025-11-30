package com.example.OrderManagementSystem.config;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.ContractType;
import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.service.ContractService;
import com.example.OrderManagementSystem.service.ContractTypeService;
import com.example.OrderManagementSystem.service.CustomerService;
import com.example.OrderManagementSystem.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final int REQUIRED_SEED_COUNT = 10;

    // We inject all the services we need to create our entities
    private final CustomerService customerService;
    private final ContractTypeService contractTypeService;
    private final ContractService contractService;
    private final OrderService orderService;

    public DataInitializer(CustomerService customerService,
                           ContractTypeService contractTypeService,
                           ContractService contractService,
                           OrderService orderService) {
        this.customerService = customerService;
        this.contractTypeService = contractTypeService;
        this.contractService = contractService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- Ensuring database contains minimum seed data ---");
        List<ContractType> contractTypes = seedContractTypes();
        List<Customer> customers = seedCustomers();
        List<Contract> contracts = seedContracts(contractTypes);
        seedOrders(customers, contracts);
        log.info("--- Data initialization complete ---");
    }

    private List<ContractType> seedContractTypes() {
        List<ContractType> existing = contractTypeService.findAll();
        if (existing.size() >= REQUIRED_SEED_COUNT) {
            log.info("Skipping contract type seeding, found {} records.", existing.size());
            return existing;
        }

        String[][] typeDefinitions = {
                {"Standard Service Agreement", "STD-SVC"},
                {"Premium Managed Services", "PREM-MSP"},
                {"Hardware Maintenance", "HW-MAINT"},
                {"Software Subscription", "SW-SUB"},
                {"Consulting Retainer", "CONSULT"},
                {"Cloud Hosting SLA", "CLOUD-SLA"},
                {"Cybersecurity Coverage", "SECURE"},
                {"Integration Support", "INTEGRATE"},
                {"Training Package", "TRAIN"},
                {"Onsite Support", "ONSITE"}
        };

        int created = 0;
        int missing = REQUIRED_SEED_COUNT - existing.size();
        for (int i = 0; i < typeDefinitions.length && created < missing; i++) {
            ContractType type = new ContractType();
            type.setName(typeDefinitions[i][0]);
            type.setType(typeDefinitions[i][1]);
            contractTypeService.save(type);
            created++;
        }
        log.info("Seeded {} contract types.", created);
        return contractTypeService.findAll();
    }

    private List<Customer> seedCustomers() {
        List<Customer> existing = customerService.findAll();
        if (existing.size() >= REQUIRED_SEED_COUNT) {
            log.info("Skipping customer seeding, found {} records.", existing.size());
            return existing;
        }

        String[][] customerDefinitions = {
                {"Acme Corporation", "USD", "contact@acme.com"},
                {"Globex Industries", "EUR", "info@globex.com"},
                {"Initech LLC", "USD", "hello@initech.com"},
                {"Umbrella Health", "GBP", "support@umbrellahealth.com"},
                {"Wayne Enterprises", "USD", "service@wayneenterprises.com"},
                {"Stark Solutions", "USD", "partners@starksolutions.com"},
                {"Wonka Foods", "EUR", "sales@wonkafoods.com"},
                {"Soylent Systems", "GBP", "team@soylentsystems.com"},
                {"Hooli Labs", "USD", "labs@hooli.com"},
                {"Tyrell Analytics", "EUR", "contact@tyrellanalytics.com"}
        };

        int created = 0;
        int missing = REQUIRED_SEED_COUNT - existing.size();
        for (int i = 0; i < customerDefinitions.length && created < missing; i++) {
            Customer customer = new Customer();
            customer.setName(customerDefinitions[i][0]);
            customer.setCurrency(customerDefinitions[i][1]);
            customer.setEmail(customerDefinitions[i][2]);
            customerService.save(customer);
            created++;
        }
        log.info("Seeded {} customers.", created);
        return customerService.findAll();
    }

    private List<Contract> seedContracts(List<ContractType> contractTypes) {
        List<Contract> existing = contractService.findAll();
        if (existing.size() >= REQUIRED_SEED_COUNT) {
            log.info("Skipping contract seeding, found {} records.", existing.size());
            return existing;
        }
        if (contractTypes.isEmpty()) {
            log.warn("Cannot seed contracts because no contract types exist.");
            return existing;
        }

        String[] contractNames = {
                "Enterprise Support Bundle",
                "Managed Infrastructure Pack",
                "Cloud Optimization Offer",
                "Cyber Defense Umbrella",
                "Field Service Coverage",
                "Expansion Readiness Plan",
                "Legacy Modernization Suite",
                "Business Continuity Pack",
                "Regional Rollout Agreement",
                "Innovation Accelerator"
        };

        int created = 0;
        int missing = REQUIRED_SEED_COUNT - existing.size();
        for (int i = 0; i < contractNames.length && created < missing; i++) {
            Contract contract = new Contract();
            contract.setName(contractNames[i]);
            contract.setStatus(i % 4 == 0 ? Contract.ContractStatus.DOWN : Contract.ContractStatus.ACTIVE);
            ContractType type = contractTypes.get((existing.size() + created) % contractTypes.size());
            contract.setContractType(type);
            contractService.save(contract);
            created++;
        }
        log.info("Seeded {} contracts.", created);
        return contractService.findAll();
    }

    private void seedOrders(List<Customer> customers, List<Contract> contracts) {
        List<Order> existing = orderService.findAll();
        if (existing.size() >= REQUIRED_SEED_COUNT) {
            log.info("Skipping order seeding, found {} records.", existing.size());
            return;
        }
        if (customers.isEmpty()) {
            log.warn("Cannot seed orders because no customers exist.");
            return;
        }

        String[] orderNames = {
                "Q1 Hardware Refresh",
                "Q2 Software Deployment",
                "Q3 Expansion Kits",
                "Q4 Replenishment",
                "Emergency Spares",
                "Branch Upgrade Pack",
                "Audit Remediation",
                "Pilot Rollout",
                "Seasonal Surge",
                "End-of-Year Optimization"
        };
        String[] addresses = {
                "123 Main St, Anytown, USA",
                "987 Industrial Way, Berlin",
                "456 Market St, Brussels",
                "22 Fleet St, London",
                "77 Harbour Rd, Sydney",
                "90 Sunset Blvd, Los Angeles",
                "15 Rue de Lyon, Paris",
                "200 Innovation Dr, Austin",
                "55 Bay St, Toronto",
                "3 Marunouchi, Tokyo"
        };

        int created = 0;
        int missing = REQUIRED_SEED_COUNT - existing.size();
        for (int i = 0; i < orderNames.length && created < missing; i++) {
            Order order = new Order();
            order.setName(orderNames[i]);
            order.setShippingAddress(addresses[i]);
            order.setCustomer(customers.get((existing.size() + created) % customers.size()));
            if (!contracts.isEmpty()) {
                order.setContract(contracts.get((existing.size() + created) % contracts.size()));
            }
            orderService.save(order);
            created++;
        }
        log.info("Seeded {} orders.", created);
    }
}
