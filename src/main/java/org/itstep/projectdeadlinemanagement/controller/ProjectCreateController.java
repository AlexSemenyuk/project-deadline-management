package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/projects/project_create")
public class ProjectCreateController {
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final ProjectListRepository projectListRepository;
    private final ProjectStatusRepository projectStatusRepository;

    @GetMapping
    public String home(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("projectConditions", projectConditionRepository.findAll());
        model.addAttribute("project_lists", projectListRepository.findAll());
        return "project_create";
    }

    @PostMapping
    public String create(ProjectCommand command) {
        log.info("ProjectCommand {}", command);
        Optional<Customer> optionalCustomer = customerRepository.findById(command.customerId());
        Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(1);
        Optional<ProjectStatus> optionalProjectStatus = projectStatusRepository.findById(1);
        if (optionalCustomer.isPresent() &&
                optionalProjectCondition.isPresent() &&
                optionalProjectStatus.isPresent()
        ) {
            Customer customer = optionalCustomer.get();
            ProjectCondition projectCondition = optionalProjectCondition.get();
            ProjectStatus projectStatus = optionalProjectStatus.get();

            Project project = Project.fromCommand(command);
            project.setCustomer(customer);
            project.setProjectCondition(projectCondition);

            project.setDesignStatus(projectStatus);
            project.setTechnologyStatus(projectStatus);
            project.setContractStatus(projectStatus);

            ProjectList projectList = new ProjectList();
            project.setProjectList(projectList);
            projectRepository.save(project);
        }
        return "redirect:/projects";
    }

}




