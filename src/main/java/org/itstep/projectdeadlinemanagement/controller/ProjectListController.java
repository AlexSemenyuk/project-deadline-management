package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ProjectListCommand;
import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.itstep.projectdeadlinemanagement.model.ProjectList;
import org.itstep.projectdeadlinemanagement.repository.AssemblyRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectListRepository;
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
@RequestMapping("/projects/project_lists")
public class ProjectListController {
    private final AssemblyRepository assemblyRepository;
    private final ProjectListRepository projectListRepository;

    @GetMapping
    public String home(Model model) {
        List<Assembly> assemblies = assemblyRepository.findAll();
        model.addAttribute("assemblies", assemblies);
        model.addAttribute("project_lists", projectListRepository.findAll());
        return "project_lists";
    }

    @PostMapping
    public String create(ProjectListCommand command) {
        log.info("ProjectListCommand {}", command);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        optionalAssembly.ifPresent(assembly -> {
            ProjectList projectList = ProjectList.fromCommand(command);
            projectList.setAssembly(assembly);
            projectListRepository.save(projectList);
        });
        return "redirect:/projects/project_lists";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<ProjectList> optionalProjectLists = projectListRepository.findById(id);
        optionalProjectLists.ifPresent(projectList -> projectListRepository.deleteById(id));
        return "redirect:/projects/project_lists";
    }
}

