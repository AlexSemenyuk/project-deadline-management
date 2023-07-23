package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.ContractTypeCommand;
import org.itstep.projectdeadlinemanagement.model.ContractType;
import org.itstep.projectdeadlinemanagement.repository.ContractTypeRepository;
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
@RequestMapping("/contracts/types")
@Slf4j
public class ContractTypesController {
    private final ContractTypeRepository contractTypeRepository;

    @GetMapping
    String findAll(Model model) {
        model.addAttribute("contractTypes", contractTypeRepository.findAll());
        return "contract_types";
    }

    @PostMapping
    String create(@ModelAttribute @Validated ContractTypeCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        ContractType contractType = ContractType.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                contractTypeRepository.save(contractType);
                model.addFlashAttribute("message", "Contract type created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating Contract type because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating Contract type because of non unique division type name");
        }
        return "redirect:/contracts/types";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<ContractType> optionalContractType = contractTypeRepository.findById(id);
        optionalContractType.ifPresent(contractType -> contractTypeRepository.deleteById(id));
        return "redirect:/contracts/types";
    }
}








