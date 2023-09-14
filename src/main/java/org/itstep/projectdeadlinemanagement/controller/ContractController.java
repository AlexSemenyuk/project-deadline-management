package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/contracts")
public class ContractController {

    private final ProjectRepository projectRepository;

    @GetMapping
    public String home(Model model) {
        List<Project> tmpProjects = projectRepository.findAll();
        List<Project> projects = new ArrayList<>();
        for (Project p:tmpProjects){
            if ((p.getProjectCondition().getName().equals("Design") ||
                    p.getProjectCondition().getName().equals("Technology") ||
            p.getProjectCondition().getName().equals("Production")) &&
                    p.getContractStatus().getName().equals("Work")){
                projects.add(p);
            }
        }
        model.addAttribute("projects", projects);
        return "contracts";
    }

    @PostMapping
    public String create(Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        return "redirect:/contracts/project_contracts/" + id;
    }

}

