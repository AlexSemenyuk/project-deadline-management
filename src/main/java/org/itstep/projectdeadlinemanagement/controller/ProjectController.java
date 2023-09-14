package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;
    @GetMapping
    public String home(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @PostMapping
    public String select(Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        return "redirect:/projects/project_details/" + id;
    }

    @GetMapping("/details/{projectNumber}")
    public String datails(@PathVariable Integer projectNumber, Model model) {
        System.out.println("projectNumber = " + projectNumber);
        List<Project> projects = projectRepository.findAll();
        int id = 0;
        System.out.println("id = " + id);
        if (!projects.isEmpty()) {
            for (Project project: projects){
                System.out.println("project.getNumber() = " + project.getNumber());
                if (Objects.equals(project.getNumber(), projectNumber)){
                    id = project.getId();
                    break;
                }
            }
        }
        System.out.println("id = " + id);
        return "redirect:/projects/project_details/" + id;
    }
}

