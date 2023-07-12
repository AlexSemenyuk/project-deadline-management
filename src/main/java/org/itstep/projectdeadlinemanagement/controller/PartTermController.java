package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.PartTermCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.itstep.projectdeadlinemanagement.repository.PartTermRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/part_terms")
@RequiredArgsConstructor
@Slf4j
public class PartTermController {
    private final PartRepository partRepository;
    private final EquipmentRepository equipmentRepository;
    private final PartTermRepository partTermRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("parts", partRepository.findAll());
        model.addAttribute("equipments", equipmentRepository.findAll());
        model.addAttribute("part_terms", partTermRepository.findAll());
        return "part_terms";
    }

    @PostMapping
    public String create(PartTermCommand command) {
        log.info("TermCommand {}", command);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalPart.isPresent() && optionalEquipment.isPresent()){
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();
            PartTerm partTerm = PartTerm.fromCommand(command);
            partTerm.setPart(part);
            partTerm.setEquipment(equipment);
            partTermRepository.save(partTerm);
        }
        return "redirect:/part_terms";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<PartTerm> optionalTerm = partTermRepository.findById(id);
        optionalTerm.ifPresent(term -> partTermRepository.deleteById(id));
        return "redirect:/part_terms";
    }
}


