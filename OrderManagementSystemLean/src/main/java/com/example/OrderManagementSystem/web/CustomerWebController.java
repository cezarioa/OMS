package com.example.OrderManagementSystem.web;

import com.example.OrderManagementSystem.model.Customer;
import com.example.OrderManagementSystem.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers") // Base URL for all customer web pages
public class CustomerWebController {

    private final CustomerService customerService;

    // We inject the *exact same service* used by the API controller.
    // The business logic is reused.
    public CustomerWebController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /customers
     * Shows the full list of customers.
     * 'Model' is the object we use to pass data from the controller to the template.
     */
    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        // This string "customers/index" tells Spring to render the template at:
        // 'src/main/resources/templates/customers/index.html'
        return "customers/index";
    }

    /**
     * GET /customers/{id}
     * Shows the details page for an existing customer. (NEW)
     */
    @GetMapping("/{id}")
    public String getCustomerDetails(@PathVariable("id") Long id, Model model) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        model.addAttribute("customer", customer);
        return "customers/details";
    }

    /**
     * GET /customers/new
     * Shows the blank form to create a new customer.
     */
    @GetMapping("/new")
    public String showNewCustomerForm(Model model) {
        // We add an empty customer object to the model.
        // This object will be "filled" by the form's input fields.
        model.addAttribute("customer", new Customer());
        // This tells Spring to render:
        // 'src/main/resources/templates/customers/form.html'
        return "customers/form";
    }

    /**
     * GET /customers/{id}/edit
     * Shows the populated form to edit an existing customer. (NEW)
     */
    @GetMapping("/{id}/edit")
    public String showEditCustomerForm(@PathVariable("id") Long id, Model model) {
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        model.addAttribute("customer", customer);
        // Reuses the form template
        return "customers/form";
    }

    /**
     * POST /customers
     * Processes the form submission for creating or updating a customer.
     *
     * @ModelAttribute binds the form data to the 'customer' object.
     */
    @PostMapping
    public String saveCustomer(@ModelAttribute Customer customer) {
        Customer savedCustomer = customerService.save(customer);
        // Redirect to the details page of the saved/updated customer
        return "redirect:/customers/" + savedCustomer.getId();
    }

    /**
     * POST /customers/{id}/delete
     * Deletes a customer.
     *
     * @PathVariable reads the 'id' from the URL.
     */
    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteById(id);
        return "redirect:/customers";
    }
}