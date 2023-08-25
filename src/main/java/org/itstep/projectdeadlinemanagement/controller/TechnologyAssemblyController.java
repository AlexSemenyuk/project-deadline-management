package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.TechnologyAssemblyCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.PartOrAssemblyService;
import org.itstep.projectdeadlinemanagement.service.ProjectListService;
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
@RequestMapping("technologies/technology_terms/technology_assemblies")
@RequiredArgsConstructor
@Slf4j
public class TechnologyAssemblyController {
    private final ProjectRepository projectRepository;
    private final EquipmentRepository equipmentRepository;
    private final AssemblyRepository assemblyRepository;
    private final TechnologyAssemblyRepository technologyAssemblyRepository;
    private final ProjectListService projectListService;
    private final PartOrAssemblyService partOrAssemblyService;

    @GetMapping("/{id}")
    public String findAllTechnologyAssembly(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);

        optionalProject.ifPresent(project -> {
            model.addAttribute("technologyProject", project);
//            model.addAttribute("assemblies", assemblyRepository.findAll());

            // Уникальные сборки проекта c количеством на проект
            List<AssemblyList> assemblyLists = projectListService.getAllAssemblyListsWithAmountOnProject(project.getProjectList());
            model.addAttribute("assemblyLists", assemblyLists);

            List<TechnologyAssembly> technologyAssemblies = new CopyOnWriteArrayList<>();
            for (AssemblyList assemblyList : assemblyLists) {
                technologyAssemblies.addAll(assemblyList.getAssembly().getTechnologyAssemblies());
            }
            model.addAttribute("technologyAssemblies", technologyAssemblies);
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

    @GetMapping("/assembly_details/{projectAndAssemblyId}")
    public String assemblyDetail(@PathVariable String projectAndAssemblyId, Model model) {
        model.addAttribute("projectAndPartId", projectAndAssemblyId);
//        System.out.println("id = " + id);
        String [] tmp = projectAndAssemblyId.split(":");
        int projectNumber = Integer.parseInt(tmp[0]);
        int assemblyNumber = Integer.parseInt(tmp[1]);
//        System.out.println("projectNumber = " + projectNumber);
//        System.out.println("assemblyNumber = " + assemblyNumber);
        Project project = partOrAssemblyService.findProject(projectNumber);
        model.addAttribute("project", project);

        List<Task> tasks = partOrAssemblyService.findTasks(project.getTasks(), assemblyNumber);
        model.addAttribute("tasks", tasks);

        String assembly = tasks.get(0).getPartOrAssemblyNumber() + "-" + tasks.get(0).getPartOrAssemblyName();
        model.addAttribute("assembly", assembly);

        return "technology_assembly_details";
    }

}
