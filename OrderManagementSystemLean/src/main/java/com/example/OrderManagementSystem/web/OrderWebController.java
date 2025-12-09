package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Contract;
import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.model.OrderLine;
import com.example.OrderManagementSystem.model.SellableItem;
import com.example.OrderManagementSystem.model.UnitOfMeasure;
import com.example.OrderManagementSystem.service.ContractService;
import com.example.OrderManagementSystem.service.CustomerService;
import com.example.OrderManagementSystem.service.OrderService;
import com.example.OrderManagementSystem.service.SellableItemService;
import com.example.OrderManagementSystem.service.UnitOfMeasureService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderWebController {

    private static final Logger log = LoggerFactory.getLogger(OrderWebController.class);
    private static final int MIN_ORDER_LINES = 3;

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ContractService contractService;
    private final SellableItemService sellableItemService;
    private final UnitOfMeasureService unitOfMeasureService;

    public OrderWebController(OrderService orderService,
                              CustomerService customerService,
                              ContractService contractService,
                              SellableItemService sellableItemService,
                              UnitOfMeasureService unitOfMeasureService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.contractService = contractService;
        this.sellableItemService = sellableItemService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(SellableItem.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    Long id = Long.valueOf(text);
                    sellableItemService.findById(id).ifPresent(this::setValue);
                } catch (NumberFormatException ignored) {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(UnitOfMeasure.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    Long id = Long.valueOf(text);
                    unitOfMeasureService.findById(id).ifPresent(this::setValue);
                } catch (NumberFormatException ignored) {
                    setValue(null);
                }
            }
        });
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/index";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        model.addAttribute("order", order);
        return "orders/details";
    }

    @GetMapping("/new")
    public String showNewOrderForm(Model model) {
        Order order = new Order();
        order.setCustomer(new Customer());
        order.setContract(new Contract());
        ensureMinimumOrderLines(order);
        prepareOrderFormModel(model, order);
        return "orders/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditOrderForm(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (order.getCustomer() == null) order.setCustomer(new Customer());
        if (order.getContract() == null) order.setContract(new Contract());
        ensureMinimumOrderLines(order);
        prepareOrderFormModel(model, order);
        return "orders/form";
    }

    @PostMapping
    public String saveOrder(@Valid @ModelAttribute("order") Order formOrder,
                            BindingResult bindingResult,
                            Model model) {

        if (formOrder.getCustomer() == null || formOrder.getCustomer().getId() == null) {
            bindingResult.rejectValue("customer.id", "NotNull", "Please select a customer.");
        }

        if (bindingResult.hasErrors()) {
            ensureMinimumOrderLines(formOrder);
            model.addAttribute("order", formOrder);
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            model.addAttribute("sellableItems", sellableItemService.findAll());
            model.addAttribute("units", unitOfMeasureService.findAll());
            return "orders/form";
        }

        Customer realCustomer = customerService.findById(formOrder.getCustomer().getId()).orElse(null);
        if (realCustomer == null) {
            bindingResult.rejectValue("customer.id", "NotFound", "Selected customer does not exist.");
            ensureMinimumOrderLines(formOrder);
            model.addAttribute("order", formOrder);
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            model.addAttribute("sellableItems", sellableItemService.findAll());
            model.addAttribute("units", unitOfMeasureService.findAll());
            return "orders/form";
        }

        Contract realContract = null;
        if (formOrder.getContract() != null && formOrder.getContract().getId() != null) {
            realContract = contractService.findById(formOrder.getContract().getId()).orElse(null);
        }

        try {
            Order orderToSave;

            if (formOrder.getId() != null) {
                log.info("Updating existing order ID: {}", formOrder.getId());
                Order existingOrder = orderService.findById(formOrder.getId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));

                existingOrder.setName(formOrder.getName());
                existingOrder.setShippingAddress(formOrder.getShippingAddress());
                existingOrder.setCustomer(realCustomer);
                existingOrder.setContract(realContract);

                orderToSave = existingOrder;
            } else {
                log.info("Creating new order");
                orderToSave = formOrder;
                orderToSave.setCustomer(realCustomer);
                orderToSave.setContract(realContract);
            }

            applyOrderLines(orderToSave, formOrder.getOrderLines());
            Order savedOrder = orderService.save(orderToSave);
            return "redirect:/orders/" + savedOrder.getId();

        } catch (Exception e) {
            log.error("Error saving order", e);
            ensureMinimumOrderLines(formOrder);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("order", formOrder);
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("contracts", contractService.findAll());
            model.addAttribute("sellableItems", sellableItemService.findAll());
            model.addAttribute("units", unitOfMeasureService.findAll());
            return "orders/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }

    private void prepareOrderFormModel(Model model, Order order) {
        model.addAttribute("order", order);
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("contracts", contractService.findAll());
        model.addAttribute("sellableItems", sellableItemService.findAll());
        model.addAttribute("units", unitOfMeasureService.findAll());
    }

    private void ensureMinimumOrderLines(Order order) {
        if (order.getOrderLines() == null) return;
        while (order.getOrderLines().size() < MIN_ORDER_LINES) {
            order.addOrderLine(new OrderLine());
        }
    }

    private void applyOrderLines(Order target, List<OrderLine> source) {
        target.getOrderLines().clear();
        if (source == null) return;
        for (OrderLine line : source) {
            if (line == null) continue;
            Optional<SellableItem> sellableItem = resolveSellableItem(line);
            Optional<UnitOfMeasure> unitOfMeasure = resolveUnitOfMeasure(line);
            if (sellableItem.isEmpty() || unitOfMeasure.isEmpty()) continue;
            if (line.getQuantity() <= 0) continue;
            line.setItem(sellableItem.get());
            line.setUnit(unitOfMeasure.get());
            line.setOrder(target);
            target.addOrderLine(line);
        }
    }

    private Optional<SellableItem> resolveSellableItem(OrderLine line) {
        if (line.getItem() == null || line.getItem().getId() == null) {
            return Optional.empty();
        }
        return sellableItemService.findById(line.getItem().getId());
    }

    private Optional<UnitOfMeasure> resolveUnitOfMeasure(OrderLine line) {
        if (line.getUnit() == null || line.getUnit().getId() == null) {
            return Optional.empty();
        }
        return unitOfMeasureService.findById(line.getUnit().getId());
    }
}
