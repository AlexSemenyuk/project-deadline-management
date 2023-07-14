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
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/terms")
public class TermsController {
    private final ProjectRepository projectRepository;

    @GetMapping
    public String home(Model model) {
        List<Project> tmpProjects = projectRepository.findAll();
        List<Project> projects = new CopyOnWriteArrayList<>();
        for (Project p:tmpProjects){
            if (p.getProjectCondition().getName().equals("Technology")){
                projects.add(p);
            }
        }
        model.addAttribute("projects", projects);
        return "terms";
    }

    @PostMapping
    public String create(Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
//        String[] redirect = new String[1];
//        redirect[0] = "redirect:/terms";
//        optionalProject.ifPresent(project -> {
//            model.addAttribute("projectTerm", project);
//            redirect[0] = "redirect:/terms/term_parts" + projectId;
//        });
        String redirect = "redirect:/terms/term_parts/" + id;
        return redirect;
    }
}
