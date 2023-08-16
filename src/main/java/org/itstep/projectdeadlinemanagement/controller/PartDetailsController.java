package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.CustomerRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.service.PartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("parts/part_details")
@RequiredArgsConstructor
@Slf4j
public class PartDetailsController {
    private final PartService partService;
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final ProjectConditionRepository projectConditionRepository;

    @GetMapping("/{id}")
    public String home(@PathVariable String id, Model model) {
        model.addAttribute("projectAndPartId", id);
//        System.out.println("id = " + id);
        String [] tmp = id.split(":");
        int projectNumber = Integer.parseInt(tmp[0]);
        int partNumber = Integer.parseInt(tmp[1]);
//        System.out.println("projectNumber = " + projectNumber);
//        System.out.println("partNumber = " + partNumber);
        Project project = partService.findProject(projectNumber);
        model.addAttribute("project", project);

        List<Task> tasks = partService.findTasks(project.getTasks(), partNumber);
        model.addAttribute("tasks", tasks);

        String part = tasks.get(0).getPartOrAssemblyNumber() + "-" + tasks.get(0).getPartOrAssemblyName();
        model.addAttribute("part", part);

        return "part_details";
    }


//    @GetMapping("delete/{id}")
//    public String delete(@PathVariable Integer id) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> projectRepository.deleteById(id));
//        return "redirect:/projects";
//    }

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

