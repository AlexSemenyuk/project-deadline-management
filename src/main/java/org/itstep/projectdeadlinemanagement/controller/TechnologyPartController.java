package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.TechnologyPartCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("technologies/technology_parts")
@RequiredArgsConstructor
@Slf4j
public class TechnologyPartController {
    private final ProjectRepository projectRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final PartRepository partRepository;
    private final EquipmentRepository equipmentRepository;
    private final TechnologyPartRepository termPartRepository;

    @GetMapping("/{id}")
    public String findAll(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        List<TechnologyPart> termPartLists = new CopyOnWriteArrayList<>();
        optionalProject.ifPresent(project -> {
            model.addAttribute("technologyProject", project);
//            model.addAttribute("projectLists", project.getProjectLists());

            project.getProjectLists().forEach(projectList -> {
                termPartLists.addAll(projectList.getPart().getTermParts());

            });
            model.addAttribute("term_parts", termPartLists);
        });
        model.addAttribute("equipments", equipmentRepository.findAll());
        return "technology_parts";
    }

    @PostMapping("/{id}")
    public String create(TechnologyPartCommand command) {
        log.info("TermCommand {}", command);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalPart.isPresent() && optionalEquipment.isPresent()) {
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();
            TechnologyPart termPart = TechnologyPart.fromCommand(command);
            termPart.setPart(part);
            termPart.setEquipment(equipment);
            termPartRepository.save(termPart);
        }
        return "redirect:/technologies/technology_parts/{id}";
    }

    @GetMapping(("{id}/delete/{termPartId}"))
    String delete(@PathVariable Integer id, @PathVariable Integer termPartId) {

        Optional<TechnologyPart> optionalTermPart = termPartRepository.findById(termPartId);
        optionalTermPart.ifPresent(term -> termPartRepository.deleteById(termPartId));
        return "redirect:/technologies/technology_parts/{id}";
    }

    @GetMapping(("finish/{id}"))
    String finish(@PathVariable Integer id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            Optional<ProjectCondition> optionalProjectCondition = projectConditionRepository.findById(3);
            optionalProjectCondition.ifPresent(condition -> {
                project.setProjectCondition(condition);
                projectRepository.save(project);
            });
        });
        return "redirect:/";
    }

    @GetMapping("/{id}/technology_terms")
    public String term(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("techProject", project);
        });
        return "technology_terms";
    }
    @PostMapping("/{id}/technology_terms")
    public String addTerm(@PathVariable Integer id, Integer technologyTerm,  Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent() && technologyTerm != null){
            Project project = optionalProject.get();
            project.setTechnologyTerm(technologyTerm);
            projectRepository.save(project);
        }
        return "redirect:/technologies/technology_parts/{id}/technology_terms";
    }
}

