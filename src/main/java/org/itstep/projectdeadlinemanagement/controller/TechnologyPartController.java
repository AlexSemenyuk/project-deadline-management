package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.TechnologyPartCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProjectListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequestMapping("technologies/technology_terms/technology_parts")
@RequiredArgsConstructor
@Slf4j
public class TechnologyPartController {
    private final ProjectRepository projectRepository;
    private final EquipmentRepository equipmentRepository;
    private final PartRepository partRepository;
    private final TechnologyPartRepository technologyPartRepository;
    private final ProjectListService projectListService;

    @GetMapping("/{id}")
    public String findAllTechnologyPart(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
//        List<TechnologyPart> termPartLists = new CopyOnWriteArrayList<>();
        optionalProject.ifPresent(project -> {
            model.addAttribute("technologyProject", project);

            // Уникальные детали проекта c количеством на проект
            List<PartList> partLists = projectListService.getAllPartListsWithAmountOnProject(project.getProjectList());
            model.addAttribute("partLists", partLists);

            List<TechnologyPart> technologyParts = new CopyOnWriteArrayList<>();
            for (PartList partList : partLists) {
                technologyParts.addAll(partList.getPart().getTechnologyParts());
            }

            model.addAttribute("technologyParts", technologyParts);
        });
        model.addAttribute("equipments", equipmentRepository.findAll());
        return "technology_parts";
    }

    @PostMapping("/{id}")
    public String createTechnologyPart(@PathVariable Integer id, TechnologyPartCommand command) {
        log.info("TechnologyPartCommand {}", command);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalPart.isPresent() && optionalEquipment.isPresent()) {
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();
            TechnologyPart technologyPart = TechnologyPart.fromCommand(command);
            technologyPart.setPart(part);
            technologyPart.setEquipment(equipment);
            technologyPartRepository.save(technologyPart);
        }
        return "redirect:/technologies/technology_terms/technology_parts/{id}";
    }

    @GetMapping(("/{id}/delete/{technologyPartId}"))
    String delete(@PathVariable Integer id, @PathVariable Integer technologyPartId) {
        Optional<TechnologyPart> optionalTermPart = technologyPartRepository.findById(technologyPartId);
        optionalTermPart.ifPresent(term -> technologyPartRepository.deleteById(technologyPartId));
        return "redirect:/technologies/technology_terms/technology_parts/{id}";
    }


    @GetMapping("/{id}/edit/{technologyPartId}")
    public String findById(@PathVariable Integer id, @PathVariable Integer technologyPartId, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        Optional<TechnologyPart> optionalTechnologyPart = technologyPartRepository.findById(technologyPartId);
        if (optionalProject.isPresent() && optionalTechnologyPart.isPresent()) {
            Project project = optionalProject.get();
            model.addAttribute("project", project);
            TechnologyPart technologyPart = optionalTechnologyPart.get();
            model.addAttribute("technologyPart", technologyPart);
        }
        List<Equipment> equipments = equipmentRepository.findAll();
        model.addAttribute("equipments", equipments);
        return "technology_part_edit";
    }

    @PostMapping("/{id}/edit/{technologyPartId}")
    public String edit(@PathVariable Integer id, @PathVariable Integer technologyPartId, TechnologyPartCommand command) {
        Optional<TechnologyPart> optionalTechnologyPart = technologyPartRepository.findById(technologyPartId);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalTechnologyPart.isPresent() &&
                optionalPart.isPresent() &&
                optionalEquipment.isPresent()) {

            TechnologyPart technologyPart = optionalTechnologyPart.get();
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();

            technologyPart.setPart(part);
            technologyPart.setNumber(command.number());
            technologyPart.setEquipment(equipment);
            technologyPart.setOperationTime(command.operationTime());
            technologyPartRepository.save(technologyPart);
        }
        return "redirect:/technologies/technology_terms/technology_parts/{id}/edit/{technologyPartId}";
    }

    public int getAllAssemblyLists(Project project) {
        int count = 0;
        count = extractAssemblyLists(project.getProjectList().getAssemblyLists(), count);
        return count;
    }

    private int extractAssemblyLists(List<AssemblyList> assemblies, int count) {
        for (AssemblyList assemblyList : assemblies) {
            Assembly assembly = assemblyList.getAssembly();
            count++;
            System.out.println("count = " + count);
            if (!assembly.getAssemblyLists().isEmpty()) {
                extractAssemblyLists(assembly.getAssemblyLists(), count);
            }
        }
        return count;
    }

    public List<PartList> getAllPartLists(Project project) {
        List<PartList> allPartLists = new ArrayList<>();
        allPartLists = extractPartLists(project.getProjectList().getAssemblyLists(), allPartLists);
        return allPartLists;
    }


    private List<PartList> extractPartLists(List<AssemblyList> assemblies, List<PartList> allPartLists) {
        for (AssemblyList assemblyList : assemblies) {
            Assembly assembly = assemblyList.getAssembly();
            if (!assembly.getAssemblyLists().isEmpty()) {
                extractPartLists(assembly.getAssemblyLists(), allPartLists);
            }
            allPartLists.addAll(assembly.getPartLists());
            System.out.println("allPartLists.size() = " + allPartLists.size());
        }
        return allPartLists;
    }

}

