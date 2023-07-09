package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;
import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.itstep.projectdeadlinemanagement.model.Part;
import org.itstep.projectdeadlinemanagement.repository.AssemblyReposutory;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/assemblies")
@RequiredArgsConstructor
@Slf4j
public class AssemblyController {
    private final PartRepository partRepository;
    private final AssemblyReposutory assemblyReposutory;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("assemblies", assemblyReposutory.findAll());
        model.addAttribute("parts", partRepository.findAll());
        return "assemblies";
    }

    @PostMapping
    public String create(AssemblyCommand command) {
        log.info("AssemblyCommand {}", command);
        if (command != null) {
            Assembly assembly = Assembly.fromCommand(command);
            List<Part> parts = partRepository.findAllById(command.partsIds());
            parts.forEach(assembly::addPart);
            assemblyReposutory.save(assembly);
        }
        return "redirect:/assemblies";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Assembly> optionalAssembly = assemblyReposutory.findById(id);
        optionalAssembly.ifPresent(assembly -> assemblyReposutory.deleteById(id));
        return "redirect:/assemblies";
    }
}














