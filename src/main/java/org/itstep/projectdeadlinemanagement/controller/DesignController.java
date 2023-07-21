package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/designs")
public class DesignController {
    private final ProjectRepository projectRepository;

    @GetMapping
    public String home(Model model) {
        List<Project> tmpProjects = projectRepository.findAll();
        List<Project> projects = new CopyOnWriteArrayList<>();
        for (Project p:tmpProjects){
            if (p.getProjectCondition().getName().equals("Design")){
                projects.add(p);
            }
        }
        model.addAttribute("projects", projects);
        return "designs";
    }

    @PostMapping
    public String create(Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        String redirect = "redirect:/designs/design_terms/" + id;
        return redirect;
    }
}