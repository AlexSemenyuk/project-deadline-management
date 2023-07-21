package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.CustomerCommand;
import org.itstep.projectdeadlinemanagement.model.Customer;
import org.itstep.projectdeadlinemanagement.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("projects/project_create/customers")
@Slf4j
public class CustomerController {
    private final CustomerRepository customerRepository;
    @GetMapping
    String findAll(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customers";
    }

    @PostMapping
    String create(@ModelAttribute @Validated CustomerCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        Customer customer = Customer.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                customerRepository.save(customer);
                model.addFlashAttribute("message", "Customer created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating publisher because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating publisher because of non unique publisher name");
        }
        return "redirect:/projects/project_create/customers";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        optionalCustomer.ifPresent(customer -> customerRepository.deleteById(id));
        return "redirect:/projects/project_create/customers";
    }
}








