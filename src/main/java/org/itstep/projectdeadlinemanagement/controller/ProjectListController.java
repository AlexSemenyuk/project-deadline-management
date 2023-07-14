package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ProjectListCommand;
import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.itstep.projectdeadlinemanagement.model.Part;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.ProjectList;
import org.itstep.projectdeadlinemanagement.repository.AssemblyRepository;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectListRepository;
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
@RequestMapping("/projects/project_lists")
public class ProjectListController {
    //    private final AssemblyRepository assemblyRepository;
    private final ProjectRepository projectRepository;
    private final PartRepository partRepository;
    private final ProjectListRepository projectListRepository;

    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model) {
//        List<Assembly> assemblies = assemblyRepository.findAll();
//        model.addAttribute("assemblies", assemblies);
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
            model.addAttribute("project_lists", project.getProjectLists());
        });
        List<Part> parts = partRepository.findAll();
        model.addAttribute("parts", parts);
//        model.addAttribute("project_lists", projectListRepository.findAll());
        return "project_lists";
    }

    @PostMapping("/{id}")
    public String create(@PathVariable Integer id,
                         ProjectListCommand command) {
        log.info("ProjectListCommand {}", command);
//        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
//        optionalAssembly.ifPresent(assembly -> {
//            ProjectList projectList = ProjectList.fromCommand(command);
//            projectList.setAssembly(assembly);
//            projectListRepository.save(projectList);
//        });
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            Optional<Part> optionalPart = partRepository.findById(command.partId());
            optionalPart.ifPresent(part -> {
                ProjectList projectList = ProjectList.fromCommand(command);
                projectList.setPart(part);
                projectListRepository.save(projectList);
                project.getProjectLists().add(projectList);
                projectRepository.save(project);
            });
        });

        return "redirect:/projects/project_lists/{id}";
    }

    @GetMapping("/{id}/delete/{listId}")
    public String delete(@PathVariable Integer id, @PathVariable Integer listId) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            for (ProjectList item: project.getProjectLists()){
                if (Objects.equals(item.getId(), listId)){
                    project.getProjectLists().remove(item);
                    break;
                }
            }
            projectRepository.save(project);
        });
        return "redirect:/projects/project_lists/{id}";
    }

}

