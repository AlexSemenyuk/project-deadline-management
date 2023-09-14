package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;
import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.itstep.projectdeadlinemanagement.repository.AssemblyRepository;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/assemblies")
@RequiredArgsConstructor
@Slf4j
public class AssemblyController {
    private final PartRepository partRepository;
    private final AssemblyRepository assemblyRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("assemblies", assemblyRepository.findAll());
        model.addAttribute("parts", partRepository.findAll());
        return "assemblies";
    }

    @PostMapping
    public String create(AssemblyCommand command) {
        log.info("AssemblyCommand {}", command);
        if (command != null) {
            Assembly assembly = Assembly.fromCommand(command);
//            List<Part> parts = partRepository.findAllById(command.partsIds());
//            parts.forEach(assembly::addPart);
            assemblyRepository.save(assembly);
        }
        return "redirect:/assemblies";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        optionalAssembly.ifPresent(assembly -> assemblyRepository.deleteById(id));
        return "redirect:/assemblies";
    }

    @GetMapping(("edit/{id}"))
    String findById(@PathVariable Integer id, Model model) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        if (optionalAssembly.isPresent()){
            Assembly assembly = optionalAssembly.get();
            model.addAttribute("assembly", assembly);
        }
        return "assembly_edit";
    }

    @PostMapping(("edit/{id}"))
    String update(@PathVariable Integer id, AssemblyCommand command) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        if (optionalAssembly.isPresent()){
            Assembly assembly = optionalAssembly.get();
            assembly.setNumber(command.number());
            assembly.setName(command.name());
            assemblyRepository.save(assembly);
        }
        return "redirect:/assemblies/edit/{id}";
    }

}














