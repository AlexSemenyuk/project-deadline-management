package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.TechnologyAssemblyCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProjectListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("technologies/technology_terms/technology_assemblies")
@RequiredArgsConstructor
@Slf4j
public class TechnologyAssemblyController {
    private final ProjectRepository projectRepository;
    private final EquipmentRepository equipmentRepository;
    private final AssemblyRepository assemblyRepository;
    private final TechnologyAssemblyRepository technologyAssemblyRepository;
    private final ProjectListService projectListService;

    @GetMapping("/{id}")
    public String findAllTechnologyAssembly(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);

        optionalProject.ifPresent(project -> {
            model.addAttribute("technologyProject", project);
            model.addAttribute("assemblies", assemblyRepository.findAll());

            List<AssemblyList> allAssemblyLists = projectListService.getAllAssemblyLists(project.getProjectList());
            model.addAttribute("allAssemblyLists", allAssemblyLists);
        });

        model.addAttribute("equipments", equipmentRepository.findAll());
        return "technology_assemblies";
    }

    @PostMapping("/{id}")
    public String createTechnologyAssembly(@PathVariable Integer id, TechnologyAssemblyCommand command) {
        log.info("TechnologyAssemblyCommand {}", command);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalAssembly.isPresent() && optionalEquipment.isPresent()) {
            Assembly assembly = optionalAssembly.get();
            Equipment equipment = optionalEquipment.get();
            TechnologyAssembly technologyAssembly = TechnologyAssembly.fromCommand(command);
            technologyAssembly.setAssembly(assembly);
            technologyAssembly.setEquipment(equipment);
            technologyAssemblyRepository.save(technologyAssembly);
        }
        return "redirect:/technologies/technology_terms/technology_assemblies/{id}";
    }


    @GetMapping(("/{id}/delete/{technologyAssemblyId}"))
    String delete(@PathVariable Integer id, @PathVariable Integer technologyAssemblyId) {
        Optional<TechnologyAssembly> optionalTechnologyAssembly = technologyAssemblyRepository.findById(technologyAssemblyId);
        optionalTechnologyAssembly.ifPresent(technologyAssemblyRepository::delete);
        return "redirect:/technologies/technology_terms/technology_assemblies/{id}";
    }

//    @GetMapping("/{id}/edit/{technologyAssemblyId}")
//    public String findById(@PathVariable Integer id, @PathVariable Integer technologyAssemblyId, Model model) {
//        Optional<Project> optionalProject = projectRepository.findById(id);
//        Optional<TechnologyPart> optionalTechnologyPart = technologyPartRepository.findById(technologyAssemblyId);
//        if (optionalProject.isPresent() && optionalTechnologyPart.isPresent()){
//            Project project = optionalProject.get();
//            model.addAttribute("project", project);
//            TechnologyPart technologyPart = optionalTechnologyPart.get();
//            model.addAttribute("technologyPart", technologyPart);
//        }
//        List<Equipment> equipments = equipmentRepository.findAll();
//        model.addAttribute("equipments", equipments);
//        return "technology_assembly_edit";
//    }

//    @PostMapping("/{id}/edit/{technologyAssemblyId}")
//    public String edit(@PathVariable Integer id, @PathVariable Integer technologyAssemblyId, TechnologyPartCommand command) {
//        Optional<TechnologyPart> optionalTechnologyPart = technologyPartRepository.findById(technologyAssemblyId);
//        Optional<Part> optionalPart = partRepository.findById(command.partId());
//        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
//        if (optionalTechnologyPart.isPresent() &&
//                optionalPart.isPresent() &&
//                optionalEquipment.isPresent()){
//
//            TechnologyPart technologyPart = optionalTechnologyPart.get();
//            Part part = optionalPart.get();
//            Equipment equipment = optionalEquipment.get();
//
//            technologyPart.setPart(part);
//            technologyPart.setNumber(command.number());
//            technologyPart.setEquipment(equipment);
//            technologyPart.setOperationTime(command.operationTime());
//            technologyPartRepository.save(technologyPart);
//        }
//        return "redirect:/technologies/technology_terms/technology_assemblies/{id}/edit/{technologyAssemblyId}";
//    }


//    public static void processAssembly(Assembly assembly) {
//        for (AssemblyList assemblyList : assembly.getAssemblyListsEntry()) {
//            processAssembly(assemblyList.getAssembly()); // Рекурсивно обрабатываем вложенные сборки
//        }
//
//        for (PartList partList : assembly.getPartLists()) {
//            Part part = partList.getPart();
//            // Обработка детали part
//        }
//    }
//
//    for (ProjectList projectList : project.getProjectList()) {
//        for (AssemblyList assemblyList : projectList.getAssemblyLists()) {
//            processAssembly(assemblyList.getAssembly());
//        }
//
//        for (PartList partList : projectList.getPartLists()) {
//            Part part = partList.getPart();
//            // Обработка детали part
//        }
//    }


}
