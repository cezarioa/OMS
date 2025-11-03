package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Order;
import com.example.OrderManagementSystem.service.OrderService;
import com.example.OrderManagementSystem.service.CustomerService;
import com.example.OrderManagementSystem.service.ContractService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderWebController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ContractService contractService;

    public OrderWebController(OrderService orderService, CustomerService customerService, ContractService contractService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.contractService = contractService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/index";
    }

    @GetMapping("/new")
    public String showNewOrderForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("contracts", contractService.findAll());
        return "orders/form";
    }

    @PostMapping
    public String saveOrder(@ModelAttribute Order order) {
        orderService.save(order);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return "redirect:/orders";
    }
}


