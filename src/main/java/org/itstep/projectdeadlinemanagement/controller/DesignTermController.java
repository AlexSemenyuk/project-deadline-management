package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.ProjectCondition;
import org.itstep.projectdeadlinemanagement.model.ProjectStatus;
import org.itstep.projectdeadlinemanagement.repository.ProjectConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectStatusRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("designs/design_terms")
@RequiredArgsConstructor
@Slf4j
public class DesignTermController {
    private final ProjectRepository projectRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final ProjectStatusRepository projectStatusRepository;

    @GetMapping("/{id}")
    public String findAll(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> model.addAttribute("designProject", project) );
        return "design_terms";
    }

    @PostMapping("/{id}")
    public String create(@PathVariable Integer id, int designTerm) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            project.setDesignTerm(designTerm);
            projectRepository.save(project);
        });
        return "redirect:/designs/design_terms/{id}";
    }

    @GetMapping(("finish/{id}"))
    String finish(@PathVariable Integer id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
                    Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(2);
                    Optional<ProjectStatus> optionalProjectStatus = projectStatusRepository.findById(2);
                    if (optionalProjectCondition.isPresent() &&
                        optionalProjectStatus.isPresent()){

                        ProjectCondition projectCondition = optionalProjectCondition.get();
                        ProjectStatus projectStatus = optionalProjectStatus.get();
                        project.setProjectCondition(projectCondition);
                        project.setDesignStatus(projectStatus);
                        projectRepository.save(project);
                    }
                });
        return "redirect:/";
    }
}



