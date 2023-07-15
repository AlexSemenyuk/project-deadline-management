package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.TermPartCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.TermPartRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("terms/term_parts")
@RequiredArgsConstructor
@Slf4j
public class TermPartController {
    private final ProjectRepository projectRepository;
    private final PartRepository partRepository;
    private final EquipmentRepository equipmentRepository;
    private final TermPartRepository termPartRepository;

    @GetMapping("/{id}")
    public String findAll(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        List<TermPart> termPartLists = new CopyOnWriteArrayList<>();
        optionalProject.ifPresent(project -> {
            model.addAttribute("projectTerm", project);
            model.addAttribute("projectLists", project.getProjectLists());

            project.getProjectLists().forEach(projectList -> {
                termPartLists.addAll(projectList.getPart().getTermParts());

            });
            model.addAttribute("term_parts", termPartLists);
        });
        model.addAttribute("equipments", equipmentRepository.findAll());
//        model.addAttribute("term_parts", termPartRepository.findAll());   //  Не годится - Вытянуть из проекта
        return "term_parts";
    }

    @PostMapping("/{id}")
    public String create(TermPartCommand command) {
        log.info("TermCommand {}", command);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalPart.isPresent() && optionalEquipment.isPresent()) {
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();
            TermPart termPart = TermPart.fromCommand(command);
            termPart.setPart(part);
            termPart.setEquipment(equipment);
            termPartRepository.save(termPart);
        }
        return "redirect:/terms/term_parts/{id}";
    }

    @GetMapping(("{id}/delete/{termPartId}"))
    String delete(@PathVariable Integer id, @PathVariable Integer termPartId) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> {
//            for (ProjectList item: project.getProjectLists()){
//                if (Objects.equals(item.getId(), listId)){
//                    project.getProjectLists().remove(item);
//                    break;
//                }
//            }
//        });
        Optional<TermPart> optionalTermPart = termPartRepository.findById(termPartId);
        optionalTermPart.ifPresent(term -> termPartRepository.deleteById(termPartId));
        return "redirect:/terms/term_parts/{id}";
    }
//    @GetMapping("/{id}/delete/{listId}")
//    public String delete(@PathVariable Integer id, @PathVariable Integer listId) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        optionalProject.ifPresent(project -> {
//            for (ProjectList item: project.getProjectLists()){
//                if (Objects.equals(item.getId(), listId)){
//                    project.getProjectLists().remove(item);
//                    break;
//                }
//            }
//            projectRepository.save(project);
//        });
//        return "redirect:/projects/project_lists/{id}";
//    }


}

