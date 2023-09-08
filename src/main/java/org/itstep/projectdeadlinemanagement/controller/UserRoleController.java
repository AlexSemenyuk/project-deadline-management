package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.UserRoleCommand;
import org.itstep.projectdeadlinemanagement.model.UserRole;
import org.itstep.projectdeadlinemanagement.repository.UserRoleRepository;
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
@RequestMapping("user_roles")
@Slf4j
public class UserRoleController {
    private final UserRoleRepository userRoleRepository;

    @GetMapping
    String findAll(Model model) {
        model.addAttribute("userRoles", userRoleRepository.findAll());
        return "user_roles";
    }

    @PostMapping
    String create(@ModelAttribute @Validated UserRoleCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        UserRole userRole = UserRole.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                userRoleRepository.save(userRole);
                model.addFlashAttribute("message", "User role created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating User role because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating User role because of non unique name");
        }
        return "redirect:/user_roles";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<UserRole> optionalUserRole = userRoleRepository.findById(id);
        optionalUserRole.ifPresent(userRole -> userRoleRepository.deleteById(id));
        return "redirect:/user_roles";
    }

    @GetMapping(("edit/{id}"))
    String findById(@PathVariable Integer id, Model model) {
        Optional<UserRole> optionalUserRole = userRoleRepository.findById(id);
        if (optionalUserRole.isPresent()){
            model.addAttribute("userRole", optionalUserRole.get());
        }
        return "user_role_edit";
    }
    @PostMapping(("edit/{id}"))
    String edit(@PathVariable Integer id, @ModelAttribute @Validated UserRoleCommand command,
                BindingResult bindingResult,
                RedirectAttributes model) {
        Optional<UserRole> optionalUserRole = userRoleRepository.findById(id);
        if (optionalUserRole.isPresent()){
            UserRole userRole = optionalUserRole.get();
            userRole.setName(command.name());
            userRoleRepository.save(userRole);
        }
        return "redirect:/user_roles/edit/{id}";
    }
}






