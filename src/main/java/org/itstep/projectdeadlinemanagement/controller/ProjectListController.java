package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.AssemblyListCommand;
import org.itstep.projectdeadlinemanagement.command.PartListCommand;
import org.itstep.projectdeadlinemanagement.command.ProjectListCommand;
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

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/projects/project_details/project_lists")
public class ProjectListController {
    private final ProjectRepository projectRepository;
    private final AssemblyRepository assemblyRepository;
    private final PartRepository partRepository;
    private final ProjectListRepository projectListRepository;
    private final PartListRepository partListRepository;
    private final AssemblyListRepository assemblyListRepository;

    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
            List<AssemblyList> assemblyLists = project.getProjectList().getAssemblyLists();
            model.addAttribute("assemblyLists", assemblyLists);
            List<PartList> partLists = project.getProjectList().getPartLists();
            model.addAttribute("partLists", partLists);
        });
        List<Part> parts = partRepository.findAll();
        model.addAttribute("parts", parts);
        return "project_lists";
    }



//    @PostMapping("/{id}")
//    public String create(@PathVariable Integer id,
//                         ProjectListCommand command) {
//        log.info("ProjectListCommand {}", command);
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> {
//            Optional<Part> optionalPart = partRepository.findById(command.partId());
//            optionalPart.ifPresent(part -> {
////                ProjectList projectList = ProjectList.fromCommand(command);
//                ProjectList projectList = new ProjectList();
////                projectList.setPart(part);
//                projectListRepository.save(projectList);
//                project.getProjectLists().add(projectList);
//                projectRepository.save(project);
//            });
//        });
//        return "redirect:/projects/project_details/project_lists/{id}";
//    }

//    @GetMapping("/{id}/edit/{listId}")
//    public String findById(@PathVariable Integer id, @PathVariable Integer listId, Model model) {
//
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        Optional<ProjectList> optionalProjectList = projectListRepository.findById(listId);
//
//        if (optionalProject.isPresent() && optionalProjectList.isPresent()){
//            Project project = optionalProject.get();
//            ProjectList projectList = optionalProjectList.get();
//            model.addAttribute("project", project);
//            model.addAttribute("projectList", projectList);
//        }
//        List<Part> parts = partRepository.findAll();
//        model.addAttribute("parts", parts);
//        return "project_list_edit";
//    }

//    @PostMapping("/{id}/edit/{listId}")
//    public String edit(@PathVariable Integer id, @PathVariable Integer listId, ProjectListCommand command) {
//
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        Optional<ProjectList> optionalProjectList = projectListRepository.findById(listId);
//        Optional<Part> optionalPart = partRepository.findById(command.partId());
//
//        if (optionalProject.isPresent() &&
//                optionalProjectList.isPresent() &&
//                optionalPart.isPresent()){
//            Project project = optionalProject.get();
//            ProjectList projectList = optionalProjectList.get();
//            Part part = optionalPart.get();
////            for (ProjectList pList: project.getProjectList()){
////                if (Objects.equals(pList.getId(), projectList.getId())){
////                    pList.setPart(part);
////                    pList.setAmount(command.amount());
////                    projectListRepository.save(pList);
////                    break;
////                }
////            }
//        }
//        return "redirect:/projects/project_details/project_lists/{id}/edit/{listId}";
//    }


//    @GetMapping("/{id}/delete/{listId}")
//    public String delete(@PathVariable Integer id, @PathVariable Integer listId) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> {
////            for (ProjectList item: project.getProjectLists()){
////                if (Objects.equals(item.getId(), listId)){
////                    project.getProjectLists().remove(item);
////                    break;
////                }
////            }
//            projectRepository.save(project);
//        });
//        return "redirect:/projects/project_details/project_lists/{id}";
//    }

}

