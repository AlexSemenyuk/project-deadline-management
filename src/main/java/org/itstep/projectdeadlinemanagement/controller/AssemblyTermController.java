package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.AssemblyTermCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.AssemblyTermRepository;
import org.itstep.projectdeadlinemanagement.repository.AssemblyRepository;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/assembly_terms")
@RequiredArgsConstructor
@Slf4j
public class AssemblyTermController {
    private final AssemblyRepository assemblyRepository;
    private final EquipmentRepository equipmentRepository;
    private final AssemblyTermRepository assemblyTermRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("assemblies", assemblyRepository.findAll());
        model.addAttribute("equipments", equipmentRepository.findAll());
        model.addAttribute("assembly_terms", assemblyTermRepository.findAll());
        return "assembly_terms";
    }

    @PostMapping
    public String create(AssemblyTermCommand command) {
        log.info("AssemblyTermRepository {}", command);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalAssembly.isPresent() && optionalEquipment.isPresent()){
            Assembly assembly = optionalAssembly.get();
            Equipment equipment = optionalEquipment.get();
            AssemblyTerm assemblyTerm = AssemblyTerm.fromCommand(command);
            assemblyTerm.setAssembly(assembly);
            assemblyTerm.setEquipment(equipment);
            assemblyTermRepository.save(assemblyTerm);
        }
        return "redirect:/assembly_terms";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<AssemblyTerm> optionalAssemblyTerm = assemblyTermRepository.findById(id);
        optionalAssemblyTerm.ifPresent(part -> assemblyTermRepository.deleteById(id));
        return "redirect:/assembly_terms";
    }

}



