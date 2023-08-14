package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;
import org.itstep.projectdeadlinemanagement.model.Customer;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.ProjectCondition;
import org.itstep.projectdeadlinemanagement.model.ProjectList;
import org.itstep.projectdeadlinemanagement.repository.CustomerRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectListRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        if (optionalCustomer.isPresent() && optionalProjectCondition.isPresent()
        ) {
            Customer customer = optionalCustomer.get();
            ProjectCondition projectCondition = optionalProjectCondition.get();
            Project project = Project.fromCommand(command);
            project.setCustomer(customer);
            project.setProjectCondition(projectCondition);

            ProjectList projectList = new ProjectList();
            project.setProjectList(projectList);
            projectRepository.save(project);
        }
        return "redirect:/projects";
    }

}




