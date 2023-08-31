package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.AssemblyListCommand;
import org.itstep.projectdeadlinemanagement.command.PartListCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.AssemblyListRepository;
import org.itstep.projectdeadlinemanagement.repository.AssemblyRepository;
import org.itstep.projectdeadlinemanagement.repository.PartListRepository;
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
@RequestMapping("/assemblies/structure")
@RequiredArgsConstructor
@Slf4j
public class AssemblyStructureController {
    private final AssemblyRepository assemblyRepository;
    private final PartRepository partRepository;
    private final PartListRepository partListRepository;
    private final AssemblyListRepository assemblyListRepository;
    @GetMapping("/{id}")
    public String findById(@PathVariable Integer id, Model model) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        if (optionalAssembly.isPresent()){
            Assembly assembly = optionalAssembly.get();
//            System.out.println("assembly.getAssemblyListsEntry() = " + assembly.getAssemblyListsEntry());
//            System.out.println("assembly.getAssemblyLists() = " + assembly.getAssemblyLists());
            model.addAttribute("assembly", assembly);
        }

        return "assembly_structure";
    }

    @GetMapping("/part_lists/{id}")
    public String findByIdForPartList (@PathVariable Integer id, Model model) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        if (optionalAssembly.isPresent()){
            Assembly assembly = optionalAssembly.get();
            model.addAttribute("assembly", assembly);
        }

        List<Part> parts = partRepository.findAll();
        model.addAttribute("parts", parts);
        return "part_lists";
    }

    @PostMapping("/part_lists/{id}")
    public String createPartList (@PathVariable Integer id, PartListCommand command) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        if (optionalAssembly.isPresent() && optionalPart.isPresent()){
            Assembly assembly = optionalAssembly.get();
            Part part = optionalPart.get();

            PartList partList = PartList.fromCommand(command);
            partList.setPart(part);
            partList.addAssembly(assembly);
            partListRepository.save(partList);
        }

        return "redirect:/assemblies/structure/part_lists/{id}";
    }

    @GetMapping("/part_lists/{id}/delete/{partListId}")
    String deletePartList(@PathVariable Integer id, @PathVariable Integer partListId) {
        Optional<PartList> optionalPartList = partListRepository.findById(partListId);
        optionalPartList.ifPresent(partListRepository::delete);
        return "redirect:/assemblies/structure/part_lists/{id}";
    }
    @GetMapping("/part_lists/{id}/edit/{partListId}")
    String editPartList(@PathVariable Integer id, @PathVariable Integer partListId) {
//        Optional<Customer> optionalCustomer = customerRepository.findById(id);
//        optionalCustomer.ifPresent(customer -> customerRepository.deleteById(id));
        return "redirect:/assemblies/structure/part_lists/{id}";
    }

    @GetMapping("/assembly_lists/{id}")
    public String findByIdForAssemblyList (@PathVariable Integer id, Model model) {
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(id);
        if (optionalAssembly.isPresent()){
            Assembly assembly = optionalAssembly.get();
            model.addAttribute("assembly", assembly);
        }
        List<Assembly> assemblies = assemblyRepository.findAll();
        model.addAttribute("assemblies", assemblies);
        return "assembly_lists";
    }

    @PostMapping("/assembly_lists/{id}")
    public String createAssemblyList (@PathVariable Integer id, AssemblyListCommand command) {
        Optional<Assembly> optionalAssemblyEntry = assemblyRepository.findById(id);
        Optional<Assembly> optionalAssembly = assemblyRepository.findById(command.assemblyId());
        if (optionalAssemblyEntry.isPresent() && optionalAssembly.isPresent()){
            Assembly assemblyEntry = optionalAssemblyEntry.get();
            Assembly assembly = optionalAssembly.get();

            AssemblyList assemblyList = AssemblyList.fromCommand(command);
            assemblyList.setAssembly(assembly);
            assemblyList.addAssemblyListEntry(assemblyEntry);
            assemblyListRepository.save(assemblyList);
        }

        return "redirect:/assemblies/structure/assembly_lists/{id}";
    }

    @GetMapping("/assembly_lists/{id}/delete/{assemblyListId}")
    String deleteAssemblyList(@PathVariable Integer id, @PathVariable Integer assemblyListId) {
        Optional<AssemblyList> optionalAssemblyList = assemblyListRepository.findById(assemblyListId);
        optionalAssemblyList.ifPresent(assemblyListRepository::delete);
        return "redirect:/assemblies/structure/assembly_lists/{id}";
    }
}

