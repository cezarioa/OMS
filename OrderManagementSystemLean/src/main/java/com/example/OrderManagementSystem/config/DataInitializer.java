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

import java.util.Optional;

/**
 * This component implements CommandLineRunner, which means its 'run' method
 * will be executed by Spring Boot after the application context is loaded.
 *
 * We use this to populate our in-memory repositories with initial data
 * for development and testing purposes.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

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
        log.info("--- Initializing application with seed data ---");

        // We only run this if the customer repository is empty
        if (customerService.findAll().isEmpty()) {
            log.info("No customers found, populating data...");

            // 1. Create a Customer
            Customer acme = new Customer();
            acme.setName("Acme Corporation");
            acme.setCurrency("USD");
            acme.setEmail("contact@acme.com");
            Customer savedAcme = customerService.save(acme);
            log.info("Created Customer: {}", savedAcme.getName());

            // 2. Create a second Customer
            Customer globex = new Customer();
            globex.setName("Globex Industries");
            globex.setCurrency("EUR");
            globex.setEmail("info@globex.com");
            Customer savedGlobex = customerService.save(globex);
            log.info("Created Customer: {}", savedGlobex.getName());


            // 3. Find a ContractType (we know one exists from ContractTypeRepository)
            Optional<ContractType> standardType = contractTypeService.findById(1L); // Assumes ID 1 is "Standard Customer Contract"

            if (standardType.isPresent()) {
                // 4. Create a Contract for Acme
                Contract acmeContract = new Contract();
                acmeContract.setName("Acme Main Services Agreement");
                acmeContract.setContractTypeId(standardType.get().getId());
                acmeContract.setStatus(Contract.ContractStatus.ACTIVE);
                // Note: In a real app, you'd link the contract to the customer,
                // but our model links it via the Order.
                Contract savedAcmeContract = contractService.save(acmeContract);
                log.info("Created Contract: {}", savedAcmeContract.getName());

                // 5. Create an Order for Acme
                Order acmeOrder = new Order();
                acmeOrder.setName("Q4 Widgets Order");
                acmeOrder.setShippingAddress("123 Main St, Anytown, USA");
                acmeOrder.setCustomerId(savedAcme.getId()); // Link to Acme
                acmeOrder.setContractId(savedAcmeContract.getId()); // Link to Acme's contract
                orderService.save(acmeOrder);
                log.info("Created Order: {}", acmeOrder.getName());
            } else {
                log.warn("Could not find standard ContractType to create initial data.");
            }

            // 6. Create a simple Order for Globex (no contract)
            Order globexOrder = new Order();
            globexOrder.setName("Initial Gadget Shipment");
            globexOrder.setShippingAddress("456 Market St, Brussels");
            globexOrder.setCustomerId(savedGlobex.getId()); // Link to Globex
            globexOrder.setContractId(null); // No contract
            orderService.save(globexOrder);
            log.info("Created Order: {}", globexOrder.getName());

            log.info("--- Data initialization complete ---");

        } else {
            log.info("Database already populated. Skipping data initialization.");
        }
    }
}