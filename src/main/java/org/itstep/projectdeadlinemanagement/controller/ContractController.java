package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.model.Contract;
import org.itstep.projectdeadlinemanagement.model.Division;
import org.itstep.projectdeadlinemanagement.model.DivisionType;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.repository.*;
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
@RequestMapping("/contracts")
public class ContractController {

    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;


    @GetMapping
    public String home(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "contracts";
    }

    @PostMapping
    public String create(Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        String redirect = "redirect:/contracts/project_contracts/" + id;
        return redirect;
    }

}

