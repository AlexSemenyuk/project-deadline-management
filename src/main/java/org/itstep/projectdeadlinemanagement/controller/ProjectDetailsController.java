package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.AssemblyListCommand;
import org.itstep.projectdeadlinemanagement.command.PartListCommand;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("projects/project_details")
@RequiredArgsConstructor
@Slf4j
public class ProjectDetailsController {
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final AssemblyRepository assemblyRepository;
    private final PartRepository partRepository;
    private final AssemblyListRepository assemblyListRepository;
    private final PartListRepository partListRepository;
    private final TaskConditionRepository taskConditionRepository;

    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
        });
        return "project_details";
    }


    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> projectRepository.deleteById(id));
        return "redirect:/projects";
    }

    @GetMapping(("/edit/{id}"))
    String datails(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()){
            Project project = optionalProject.get();
            model.addAttribute("project", project);
            model.addAttribute("customers", customerRepository.findAll());
            model.addAttribute("idCustomer", project.getCustomer().getId());
            model.addAttribute("idProjectCondition", project.getProjectCondition().getId());
            model.addAttribute("projectConditions", projectConditionRepository.findAll());
        }
        return "projects_edit";
    }
    @PostMapping(("edit/{id}"))
    String update(@PathVariable Integer id, ProjectCommand command) {
        log.info("ProjectCommand {}", command);
        Optional<Project> optionalProject = projectRepository.findById(id);
        Optional<Customer> optionalCustomer = customerRepository.findById(command.customerId());
        Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(command.projectConditionId());
        Optional<TaskCondition> optionalTaskCondition = taskConditionRepository.findById(6);
        if (optionalProject.isPresent() &&
                optionalProjectCondition.isPresent() &&
                optionalCustomer.isPresent()){
            Project project = optionalProject.get();
            project.setNumber(command.number());
            ProjectCondition projectCondition = optionalProjectCondition.get();
            Customer customer = optionalCustomer.get();
            project.setCustomer(customer);
            project.setStart(command.start());
            project.setDeadline(command.deadline());
            project.setProjectCondition(projectCondition);
            if (projectCondition.getName().equals("Archive")){
                TaskCondition taskCondition = optionalTaskCondition.get();
                if (!project.getTasks().isEmpty()){
                    for (Task task: project.getTasks()){
                        task.setTaskCondition(taskCondition);
                    }
                }
            }
            projectRepository.save(project);
        }
        return "redirect:/projects/project_details/edit/{id}";
    }

    @GetMapping("/assembly_lists/{id}")
    public String assemblyListForProject(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
        });
        List<Assembly> assemblies = assemblyRepository.findAll();
        model.addAttribute("assemblies", assemblies);
        return "project_lists_assembly";

    }

    @PostMapping("/assembly_lists/{id}")
    public String addAssemblyListToProject (@PathVariable Integer id, AssemblyListCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        if (optionalProject.isPresent() && optionalAssembly.isPresent()){
            ProjectList projectList = optionalProject.get().getProjectList();
            Assembly assembly = optionalAssembly.get();

            AssemblyList assemblyList = AssemblyList.fromCommand(command);
            assemblyList.setAssembly(assembly);
            assemblyList.addProjectList(projectList);
            assemblyListRepository.save(assemblyList);
        }

        return "redirect:/projects/project_details/assembly_lists/{id}";
    }

    @GetMapping("/assembly_lists/{id}/delete/{assemblyListId}")
    public String deleteAssemblyList(@PathVariable Integer id, @PathVariable Integer assemblyListId) {
        Optional<AssemblyList> optionalAssemblyList = assemblyListRepository.findById(assemblyListId);
        optionalAssemblyList.ifPresent(assemblyListRepository::delete);
        return "redirect:/projects/project_details/assembly_lists/{id}";
    }

    @GetMapping("/part_lists/{id}")
    public String partListForProject(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
        });
        List<Part> parts = partRepository.findAll();
        model.addAttribute("parts", parts);
        return "project_lists_part";
    }

    @PostMapping("/part_lists/{id}")
    public String addPartListToProject (@PathVariable Integer id, PartListCommand command) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        if (optionalProject.isPresent() && optionalPart.isPresent()){
            ProjectList projectList = optionalProject.get().getProjectList();
            Part part = optionalPart.get();

            PartList partList = PartList.fromCommand(command);
            partList.setPart(part);
            partList.addProjectList(projectList);
            partListRepository.save(partList);
        }
        return "redirect:/projects/project_details/part_lists/{id}";
    }

    @GetMapping("/part_lists/{id}/delete/{partListId}")
    public String deletePartList(@PathVariable Integer id, @PathVariable Integer partListId) {
        Optional<PartList> optionalPartList = partListRepository.findById(partListId);
        optionalPartList.ifPresent(partListRepository::delete);
        return "redirect:/projects/project_details/part_lists/{id}";
    }

}
