package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.DivisionTypeCommand;
import org.itstep.projectdeadlinemanagement.model.DivisionType;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.ProjectCondition;
import org.itstep.projectdeadlinemanagement.repository.ContractRepository;
import org.itstep.projectdeadlinemanagement.repository.DivisionTypeRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
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
@RequestMapping("contracts/project_contracts")
@Slf4j
public class ProjectContractController {
    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;
    @GetMapping("/{id}")
    public String findAll(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("projectContract", project);
        });
        return "project_contracts";
    }
//
//    @PostMapping("/{id}")
//    public String create(@PathVariable Integer id, int designTerm) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> {
//            project.setDesignTerm(designTerm);
//            projectRepository.save(project);
//        });
//        return "redirect:/designs/design_terms/{id}";
//    }
//
//    @GetMapping(("finish/{id}"))
//    String finish(@PathVariable Integer id) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> {
//            Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(2);
//            optionalProjectCondition.ifPresent(condition -> {
//                project.setProjectCondition(condition);
//                projectRepository.save(project);
//            });
//        });
//        return "redirect:/";
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//    @GetMapping
//    String findAll(Model model) {
//        model.addAttribute("divisionTypes", divisionTypeRepository.findAll());
//        return "division_types";
//    }
//
//    @PostMapping
//    String create(@ModelAttribute @Validated DivisionTypeCommand command,
//                  BindingResult bindingResult,
//                  RedirectAttributes model) {
//        DivisionType direction = DivisionType.fromCommand(command);
//        log.info(command.toString());
//        log.info(bindingResult.toString());
//        try {
//            if (!bindingResult.hasErrors()) {
//                divisionTypeRepository.save(direction);
//                model.addFlashAttribute("message", "Division type created successfully");
//            } else {
//                model.addFlashAttribute("error", "Error with fields: %s".formatted(
//                        bindingResult.getFieldErrors()
//                                .stream()
//                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
//                                .distinct()
//                                .collect(Collectors.joining(","))));
//            }
//        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
//            model.addFlashAttribute("error", "Error creating division type because of illegal argument or optimistic entry lock");
//        } catch (Exception ex) {
//            model.addFlashAttribute("error", "Error creating division type because of non unique division type name");
//        }
//        return "redirect:/divisions/types";
//    }
//
//    @GetMapping(("delete/{id}"))
//    String delete(@PathVariable Integer id) {
//        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(id);
//        optionalDivisionType.ifPresent(author -> divisionTypeRepository.deleteById(id));
//        return "redirect:/divisions/types";
//    }
}








