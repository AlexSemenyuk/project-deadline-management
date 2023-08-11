package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.DivisionTypeCommand;
import org.itstep.projectdeadlinemanagement.model.DivisionType;
import org.itstep.projectdeadlinemanagement.repository.DivisionTypeRepository;
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
@RequestMapping("divisions/types")
@Slf4j
public class DivisionTypeController {
    private final DivisionTypeRepository divisionTypeRepository;

    @GetMapping
    String findAll(Model model) {
        model.addAttribute("divisionTypes", divisionTypeRepository.findAll());
        return "division_types";
    }

    @PostMapping
    String create(@ModelAttribute @Validated DivisionTypeCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        DivisionType direction = DivisionType.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                divisionTypeRepository.save(direction);
                model.addFlashAttribute("message", "Division type created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating division type because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating division type because of non unique division type name");
        }
        return "redirect:/divisions/types";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(id);
        optionalDivisionType.ifPresent(divisionType -> divisionTypeRepository.deleteById(id));
        return "redirect:/divisions/types";
    }

    @GetMapping(("edit/{id}"))
    String findById(@PathVariable Integer id, Model model) {
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(id);
        if (optionalDivisionType.isPresent()){
            model.addAttribute("divisionType", optionalDivisionType.get());
        }
        return "division_types_edit";
    }
    @PostMapping(("edit/{id}"))
    String edit(@PathVariable Integer id, @ModelAttribute @Validated DivisionTypeCommand command,
                BindingResult bindingResult,
                RedirectAttributes model) {
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(id);
        if (optionalDivisionType.isPresent()){
            DivisionType divisionType = optionalDivisionType.get();
            divisionType.setName(command.name());
            divisionTypeRepository.save(divisionType);
        }
        return "redirect:/divisions/types/edit/{id}";
    }

}








