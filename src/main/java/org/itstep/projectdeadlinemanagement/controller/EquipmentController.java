package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.command.EquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Division;
import org.itstep.projectdeadlinemanagement.model.DivisionType;
//import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.repository.DivisionRepository;
import org.itstep.projectdeadlinemanagement.repository.DivisionTypeRepository;
//import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/equipments")
public class EquipmentController {
    private final DivisionRepository divisionRepository;
    private final EquipmentRepository equipmentRepository;

    @GetMapping
    public String home(Model model) {
        List<Equipment> equipments = equipmentRepository.findAll();
        model.addAttribute("equipments", equipments);
        List<Division> divisions = divisionRepository.findAll()
                .stream().filter(d -> d.getDivisionType().getName().equals("Production"))
                .collect(Collectors.toList());
        model.addAttribute("divisions", divisions);
        return "equipments";
    }

    @PostMapping
    public String create(EquipmentCommand command) {
        log.info("EquipmentCommand {}", command);
        Optional<Division> optionalDivision = divisionRepository.findById(command.divisionId());
        optionalDivision.ifPresent(division -> {
            Equipment equipment = Equipment.fromCommand(command);
            equipment.setDivision(division);
            equipmentRepository.save(equipment);
        });
        return "redirect:/equipments";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        optionalEquipment.ifPresent(equipment -> equipmentRepository.deleteById(id));
        return "redirect:/equipments";
    }
}


