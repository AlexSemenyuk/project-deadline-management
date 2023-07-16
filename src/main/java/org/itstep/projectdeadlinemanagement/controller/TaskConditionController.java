package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.CustomerCommand;
import org.itstep.projectdeadlinemanagement.command.TaskConditionCommand;
import org.itstep.projectdeadlinemanagement.model.Customer;
import org.itstep.projectdeadlinemanagement.model.TaskCondition;
import org.itstep.projectdeadlinemanagement.repository.CustomerRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskConditionRepository;
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
@RequestMapping("tasks/task_conditions")
@Slf4j
public class TaskConditionController {
    private final TaskConditionRepository taskConditionRepository;

    @GetMapping
    String findAll(Model model) {
        model.addAttribute("taskConditions", taskConditionRepository.findAll());
        return "task_conditions";
    }

    @PostMapping
    String create(@ModelAttribute @Validated TaskConditionCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        TaskCondition taskCondition = TaskCondition.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                taskConditionRepository.save(taskCondition);
                model.addFlashAttribute("message", "Task created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating task condition because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating task condition because of non unique publisher name");
        }
        return "redirect:/tasks/task_conditions";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(id);
        optionalTaskCondition.ifPresent(condition -> taskConditionRepository.deleteById(id));
        return "redirect:/tasks/task_conditions";
    }
}







