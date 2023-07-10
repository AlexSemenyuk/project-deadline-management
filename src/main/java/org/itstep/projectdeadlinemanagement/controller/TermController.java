package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.command.PartCommand;
import org.itstep.projectdeadlinemanagement.command.TermCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.itstep.projectdeadlinemanagement.repository.TermRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/terms")
@RequiredArgsConstructor
@Slf4j
public class TermController {
    private final PartRepository partRepository;
    private final EquipmentRepository equipmentRepository;
    private final TermRepository termRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("parts", partRepository.findAll());
        model.addAttribute("equipments", equipmentRepository.findAll());
        model.addAttribute("terms", termRepository.findAll());
        return "terms";
    }

    @PostMapping
    public String create(TermCommand command) {
        log.info("TermCommand {}", command);
        Optional<Part> optionalPart = partRepository.findById(command.partId());
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(command.equipmentId());
        if (optionalPart.isPresent() && optionalEquipment.isPresent()){
            Part part = optionalPart.get();
            Equipment equipment = optionalEquipment.get();
            Term term = Term.fromCommand(command);
            term.setPart(part);
            term.setEquipment(equipment);
            termRepository.save(term);
        }
//        Term term = Term.fromCommand(command);
//        optionalPart.ifPresent(term::setPart);
//        optionalEquipment.ifPresent(term::setEquipment);
//        termRepository.save(term);
        return "redirect:/terms";
    }


    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<Term> optionalTerm = termRepository.findById(id);
        optionalTerm.ifPresent(part -> termRepository.deleteById(id));
        return "redirect:/terms";
    }
}


