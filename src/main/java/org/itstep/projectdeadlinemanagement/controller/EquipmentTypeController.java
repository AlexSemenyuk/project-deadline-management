package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.EquipmentTypeCommand;
import org.itstep.projectdeadlinemanagement.model.EquipmentType;
import org.itstep.projectdeadlinemanagement.repository.EquipmentTypeRepository;
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
@RequestMapping("equipments/types")
@Slf4j
public class EquipmentTypeController {
    private final EquipmentTypeRepository equipmentTypeRepository;

    @GetMapping
    String findAll(Model model) {
        model.addAttribute("equipmentTypes", equipmentTypeRepository.findAll());
        return "equipment_types";
    }

    @PostMapping
    String create(@ModelAttribute @Validated EquipmentTypeCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        EquipmentType equipmentType = EquipmentType.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                equipmentTypeRepository.save(equipmentType);
                model.addFlashAttribute("message", "Equipment type created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating equipment type because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating equipment type because of non unique division type name");
        }
        return "redirect:/equipments/types";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<EquipmentType> optionalDivisionType = equipmentTypeRepository.findById(id);
        optionalDivisionType.ifPresent(divisionType -> equipmentTypeRepository.deleteById(id));
        return "redirect:/equipments/types";
    }

    @GetMapping(("edit/{id}"))
    String findById(@PathVariable Integer id, Model model) {
        Optional<EquipmentType> optionalEquipmentType = equipmentTypeRepository.findById(id);
        if (optionalEquipmentType.isPresent()){
            model.addAttribute("equipmentType", optionalEquipmentType.get());
        }
        return "equipment_types_edit";
    }
    @PostMapping(("edit/{id}"))
    String edit(@PathVariable Integer id, @ModelAttribute @Validated EquipmentTypeCommand command,
                BindingResult bindingResult,
                RedirectAttributes model) {
        Optional<EquipmentType> optionalDivisionType = equipmentTypeRepository.findById(id);
        if (optionalDivisionType.isPresent()){
            EquipmentType divisionType = optionalDivisionType.get();
            divisionType.setName(command.name());
            equipmentTypeRepository.save(divisionType);
        }
        return "redirect:/equipments/types/edit/{id}";
    }

}








