package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskRepository;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tasks")
public class TasksController {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final ProductionPlanService productionPlanService;
    private final TaskConditionRepository taskConditionRepository;
    private final EquipmentRepository equipmentRepository;
    @GetMapping
    public String home(Model model) {
        List<Project> tmpProjects = projectRepository.findAll();
        List<Project> projects = new CopyOnWriteArrayList<>();
        for (Project p:tmpProjects){
            if (p.getProjectCondition().getName().equals("Production")){
                projects.add(p);
            }
        }
        model.addAttribute("projects", projects);
        return "tasks";
    }

    @PostMapping
    public String create(Integer id, Model model) {
        List<Task> tasks = taskService.formTasks(id);
        productionPlanService.formProductionPlans(tasks);
        return "redirect:/tasks/project_tasks/" + id;
    }

    @GetMapping("project_tasks/{id}")
    public String home(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("projectWithTasks", project);
            model.addAttribute("projectTasks", project.getTasks());
        });
        return "project_tasks";
    }


}

