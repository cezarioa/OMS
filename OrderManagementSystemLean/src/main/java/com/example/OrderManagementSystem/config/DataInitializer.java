package com.example.OrderManagementSystem.config;

import com.example.OrderManagementSystem.model.*;
import com.example.OrderManagementSystem.repository.SellableItemRepository;
import com.example.OrderManagementSystem.repository.UnitOfMeasureRepository;
import com.example.OrderManagementSystem.service.ContractService;
import com.example.OrderManagementSystem.service.ContractTypeService;
import com.example.OrderManagementSystem.service.CustomerService;
import com.example.OrderManagementSystem.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final int REQUIRED_SEED_COUNT = 5;
    private static final int MIN_ORDER_LINES = 3;

    private final CustomerService customerService;
    private final ContractTypeService contractTypeService;
    private final ContractService contractService;
    private final OrderService orderService;

    private final UnitOfMeasureRepository unitRepo;
    private final SellableItemRepository itemRepo;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(CustomerService customerService,
                           ContractTypeService contractTypeService,
                           ContractService contractService,
                           OrderService orderService,
                           UnitOfMeasureRepository unitRepo,
                           SellableItemRepository itemRepo,
                           JdbcTemplate jdbcTemplate) {
        this.customerService = customerService;
        this.contractTypeService = contractTypeService;
        this.contractService = contractService;
        this.orderService = orderService;
        this.unitRepo = unitRepo;
        this.itemRepo = itemRepo;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- Starting Database Seeding & Fixes ---");

        // 1. Asigurăm existența nomenclatoarelor (Unități și Produse)
        List<UnitOfMeasure> units = seedUnits();
        List<SellableItem> items = seedItems();

        // 2. Asigurăm existența clienților și tipurilor
        List<ContractType> contractTypes = seedContractTypes();
        List<Customer> customers = seedCustomers();

        // 3. Faceți update-uri înainte de a crea noi comenzi
        ensureValidOrderDates();
        seedContracts(contractTypes, items, units);
        seedOrders(customers, contractService.findAll(), items, units);

        // 4. PAS NOU: Verificăm datele EXISTENTE și le adăugăm linii dacă nu au
        populateMissingLinesForExistingData(items, units);

        log.info("--- Database Seeding Complete ---");
    }

    // --- Metodă nouă pentru a repara datele existente ---
    private void populateMissingLinesForExistingData(List<SellableItem> items, List<UnitOfMeasure> units) {
        Random rand = new Random();

        // 1. Verificăm Contractele existente
        List<Contract> allContracts = contractService.findAll();
        for (Contract c : allContracts) {
            if (c.getContractLines().isEmpty()) {
                log.info("Adding missing lines to Contract ID: {}", c.getId());
                ContractLine line = new ContractLine();
                line.setItem(items.get(rand.nextInt(items.size())));
                line.setUnit(units.get(rand.nextInt(units.size())));
                line.setQuantity((double) (rand.nextInt(50) + 10));
                c.addContractLine(line);
                contractService.save(c);
            }
        }

        // 2. Verificăm Comenzile existente
        List<Order> allOrders = orderService.findAll();
        for (Order o : allOrders) {
            int missingLines = MIN_ORDER_LINES - o.getOrderLines().size();
            if (missingLines > 0) {
                log.info("Adding {} missing lines to Order ID: {}", missingLines, o.getId());
                for (int k = 0; k < missingLines; k++) {
                    OrderLine line = new OrderLine();
                    line.setItem(items.get(rand.nextInt(items.size())));
                    line.setUnit(units.get(rand.nextInt(units.size())));
                    line.setQuantity((double) (rand.nextInt(10) + 1));
                    o.addOrderLine(line);
                }
                orderService.save(o);
            }
        }
    }

    private List<UnitOfMeasure> seedUnits() {
        if (unitRepo.count() > 0) return unitRepo.findAll();
        unitRepo.save(new UnitOfMeasure(null, "Each", "EA"));
        unitRepo.save(new UnitOfMeasure(null, "Hours", "HR"));
        unitRepo.save(new UnitOfMeasure(null, "License", "LIC"));
        return unitRepo.findAll();
    }

    private List<SellableItem> seedItems() {
        if (itemRepo.count() > 0) return itemRepo.findAll();

        Product p1 = new Product();
        p1.setName("Laptop High-End");
        p1.setUnitValue(1500.00);
        p1.setDescription("Workstation laptop");
        itemRepo.save(p1);

        Product p2 = new Product();
        p2.setName("Office Chair");
        p2.setUnitValue(250.00);
        p2.setDescription("Ergonomic chair");
        itemRepo.save(p2);

        Product p3 = new Product();
        p3.setName("Conference Monitor");
        p3.setUnitValue(420.00);
        p3.setDescription("4K widescreen display");
        itemRepo.save(p3);

        Service s1 = new Service();
        s1.setName("IT Support Level 1");
        s1.setUnitValue(120.00);
        s1.setDescription("On-demand technical support.");
        itemRepo.save(s1);

        Service s2 = new Service();
        s2.setName("Security Assessment");
        s2.setUnitValue(320.00);
        s2.setDescription("Quarterly IT security overview.");
        itemRepo.save(s2);

        return itemRepo.findAll();
    }

    private List<ContractType> seedContractTypes() {
        List<ContractType> existing = contractTypeService.findAll();
        if (!existing.isEmpty()) return existing;
        contractTypeService.save(new ContractType(null, "Standard Agreement", "STD"));
        contractTypeService.save(new ContractType(null, "Premium SLA", "PRM"));
        return contractTypeService.findAll();
    }

    private List<Customer> seedCustomers() {
        List<Customer> existing = customerService.findAll();
        if (!existing.isEmpty()) return existing;
        customerService.save(createCustomer("Acme Corp", "USD", "contact@acme.com"));
        customerService.save(createCustomer("Global Tech", "EUR", "info@global.eu"));
        return customerService.findAll();
    }

    private Customer createCustomer(String name, String currency, String email) {
        Customer c = new Customer();
        c.setName(name);
        c.setCurrency(currency);
        c.setEmail(email);
        return c;
    }

    private void seedContracts(List<ContractType> types, List<SellableItem> items, List<UnitOfMeasure> units) {
        if (contractService.findAll().size() >= REQUIRED_SEED_COUNT) return;

        Random rand = new Random();
        for (int i = 1; i <= REQUIRED_SEED_COUNT; i++) {
            Contract c = new Contract();
            c.setName("Contract #" + i);
            c.setStatus(Contract.ContractStatus.ACTIVE);
            c.setContractType(types.get(rand.nextInt(types.size())));

            // Adăugăm linie la creare
            ContractLine line = new ContractLine();
            line.setItem(items.get(rand.nextInt(items.size())));
            line.setUnit(units.get(rand.nextInt(units.size())));
            line.setQuantity((double) (rand.nextInt(50) + 1));
            c.addContractLine(line);

            contractService.save(c);
        }
    }

    private void seedOrders(List<Customer> customers, List<Contract> contracts, List<SellableItem> items, List<UnitOfMeasure> units) {
        if (orderService.findAll().size() >= REQUIRED_SEED_COUNT) return;

        Random rand = new Random();
        for (int i = 1; i <= REQUIRED_SEED_COUNT; i++) {
            Order o = new Order();
            o.setName("Order #" + i);
            o.setShippingAddress("Street " + i);
            o.setOrderDate(LocalDate.now());
            o.setCustomer(customers.get(rand.nextInt(customers.size())));
            if (rand.nextBoolean() && !contracts.isEmpty()) {
                o.setContract(contracts.get(rand.nextInt(contracts.size())));
            }
            for (int k = 0; k < MIN_ORDER_LINES; k++) {
                OrderLine line = new OrderLine();
                line.setItem(items.get(rand.nextInt(items.size())));
                line.setUnit(units.get(rand.nextInt(units.size())));
                line.setQuantity((double) (rand.nextInt(10) + 1));
                o.addOrderLine(line);
            }
            orderService.save(o);
        }
    }

    private void ensureValidOrderDates() {
        if (jdbcTemplate == null) return;
        if (!columnExists("orders", "order_date")) return;

        LocalDate today = LocalDate.now();
        try {
            jdbcTemplate.update(
                    "UPDATE orders SET order_date = ? WHERE order_date IS NULL OR order_date = '0000-00-00'",
                    today);
        } catch (DataAccessException ex) {
            log.warn("Unable to sync order_date defaults: {}", ex.getMessage());
        }
    }

    private boolean columnExists(String table, String column) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                    Integer.class, table, column);
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            return false;
        }
    }
}
