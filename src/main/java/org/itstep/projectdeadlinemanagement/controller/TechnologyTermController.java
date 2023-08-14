package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.TechnologyAssemblyCommand;
import org.itstep.projectdeadlinemanagement.command.TechnologyPartCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("technologies/technology_terms")
@RequiredArgsConstructor
@Slf4j
public class TechnologyTermController {
    private final ProjectRepository projectRepository;
    private final ProjectConditionRepository projectConditionRepository;

    @GetMapping("/{id}")
    public String term(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("techProject", project);
        });
        return "technology_terms";
    }

    @PostMapping("/{id}")
    public String addTerm(@PathVariable Integer id, Integer technologyTerm,  Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent() && technologyTerm != null){
            Project project = optionalProject.get();
            project.setTechnologyTerm(technologyTerm);
            projectRepository.save(project);
        }
        return "redirect:/technologies/technology_terms/{id}";
    }

    @GetMapping(("finish/{id}"))
    String finish(@PathVariable Integer id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(3);
            optionalProjectCondition.ifPresent(condition -> {
                project.setProjectCondition(condition);
                projectRepository.save(project);
            });
        });
        return "redirect:/";
    }

}

