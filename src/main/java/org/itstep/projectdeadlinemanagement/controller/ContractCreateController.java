package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;
import org.itstep.projectdeadlinemanagement.model.ContractType;
import org.itstep.projectdeadlinemanagement.model.Customer;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.ProjectCondition;
import org.itstep.projectdeadlinemanagement.repository.ContractTypeRepository;
import org.itstep.projectdeadlinemanagement.repository.CustomerRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectConditionRepository;
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
@RequestMapping("contracts/project_contracts/contract_create")
@RequiredArgsConstructor
@Slf4j
public class ContractCreateController {
    private final ProjectRepository projectRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final ProjectConditionRepository projectConditionRepository;


    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
        });
        List<ContractType> contractTypes = contractTypeRepository.findAll();
        model.addAttribute("contractTypes", contractTypes);
        return "contract_create";
    }


//    @GetMapping("delete/{id}")
//    public String delete(@PathVariable Integer id) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> projectRepository.deleteById(id));
//        return "redirect:/projects";
//    }
//
//    @GetMapping(("/edit/{id}"))
//    String datails(@PathVariable Integer id, Model model) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        if (optionalProject.isPresent()){
//            Project project = optionalProject.get();
//            model.addAttribute("project", project);
//            model.addAttribute("customers", customerRepository.findAll());
//            model.addAttribute("idCustomer", project.getCustomer().getId());
//            model.addAttribute("idProjectCondition", project.getProjectCondition().getId());
//            model.addAttribute("projectConditions", projectConditionRepository.findAll());
//        }
//        return "projects_edit";
//    }
//    @PostMapping(("edit/{id}"))
//    String update(@PathVariable Integer id, ProjectCommand command) {
//        log.info("ProjectCommand {}", command);
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        Optional<Customer> optionalCustomer = customerRepository.findById(command.customerId());
//        Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(command.projectConditionId());
//        if (optionalProject.isPresent() &&
//                optionalProjectCondition.isPresent() &&
//                optionalCustomer.isPresent()){
//            Project project = optionalProject.get();
//            project.setNumber(command.number());
//            ProjectCondition projectCondition = optionalProjectCondition.get();
//            Customer customer = optionalCustomer.get();
//            project.setCustomer(customer);
//            project.setProjectCondition(projectCondition);
//            project.setStart(command.start());
//            project.setDeadline(command.deadline());
//            projectRepository.save(project);
//        }
//        return "redirect:/projects/project_details/edit/{id}";
//    }
}

