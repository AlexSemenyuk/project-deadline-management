package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.ProjectConditionCommand;
import org.itstep.projectdeadlinemanagement.model.ProjectCondition;
import org.itstep.projectdeadlinemanagement.repository.ProjectConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectStatusRepository;
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
@RequestMapping("projects/project_create/project_conditions")
@Slf4j
public class ProjectConditionController {
    private final ProjectConditionRepository projectConditionRepository;
    private final ProjectStatusRepository projectStatusRepository;

    @GetMapping
    String findAll(Model model) {
        model.addAttribute("projectConditions", projectConditionRepository.findAll());
        model.addAttribute("projectStatuses", projectStatusRepository.findAll());
        return "project_conditions";
    }

    @PostMapping
    String create(@ModelAttribute @Validated ProjectConditionCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        ProjectCondition projectCondition = ProjectCondition.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                projectConditionRepository.save(projectCondition);
                model.addFlashAttribute("message", "Condition created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating Condition because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating Condition because of non unique publisher name");
        }
        return "redirect:/projects/project_create/project_conditions";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(id);
        optionalProjectCondition.ifPresent(condition -> projectConditionRepository.deleteById(id));
        return "redirect:/projects/project_create/project_conditions";
    }

}








