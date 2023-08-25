package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.EquipmentCommand;
import org.itstep.projectdeadlinemanagement.model.Division;
//import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.EquipmentType;
import org.itstep.projectdeadlinemanagement.repository.DivisionRepository;
import org.itstep.projectdeadlinemanagement.repository.EquipmentTypeRepository;
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

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/equipments")
public class EquipmentController {
    private final DivisionRepository divisionRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final EquipmentRepository equipmentRepository;

    @GetMapping
    public String home(Model model) {
        List<Equipment> equipments = equipmentRepository.findAll();
        model.addAttribute("equipments", equipments);
        List<Division> divisions = divisionRepository.findAll();
        model.addAttribute("divisions", divisions);
        List<EquipmentType> equipmentTypes = equipmentTypeRepository.findAll();
        model.addAttribute("types", equipmentTypes);
        return "equipments";
    }

    @PostMapping
    public String create(EquipmentCommand command) {
        log.info("EquipmentCommand {}", command);
        Optional<Division> optionalDivision = divisionRepository.findById(command.divisionId());
        Optional<EquipmentType> optionalEquipmentType = equipmentTypeRepository.findById(command.equipmentTypeId());
        if (optionalDivision.isPresent() && optionalEquipmentType.isPresent()){
            Division division = optionalDivision.get();
            EquipmentType equipmentType = optionalEquipmentType.get();
            Equipment equipment = Equipment.fromCommand(command);
            equipment.setDivision(division);
            equipment.setEquipmentType(equipmentType);
            equipmentRepository.save(equipment);
        }
        return "redirect:/equipments";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        optionalEquipment.ifPresent(equipment -> equipmentRepository.deleteById(id));
        return "redirect:/equipments";
    }

    @GetMapping("edit/{id}")
    public String findById(@PathVariable Integer id, Model model) {
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        List<Division> divisions = divisionRepository.findAll();
        model.addAttribute("divisions", divisions);
        List<EquipmentType> equipmentTypes = equipmentTypeRepository.findAll();
        model.addAttribute("types", equipmentTypes);

        if (optionalEquipment.isPresent()){
            Equipment equipment = optionalEquipment.get();
            model.addAttribute("equipment", equipment);
            model.addAttribute("divisionId", equipment.getDivision().getId());
            model.addAttribute("equipmentTypeId", equipment.getEquipmentType().getId());
        }
        return "equipment_edit";
    }

    @PostMapping("edit/{id}")
    public String edit(@PathVariable Integer id, EquipmentCommand command) {
        Optional<Equipment> optionalEquipment = equipmentRepository.findById(id);
        Optional<Division> optionalDivision = divisionRepository.findById(command.divisionId());
        Optional<EquipmentType> optionalEquipmentType = equipmentTypeRepository.findById(command.equipmentTypeId());

        if (optionalEquipment.isPresent() &&
                optionalDivision.isPresent() &&
                optionalEquipmentType.isPresent() ){

            Equipment equipment = optionalEquipment.get();

            equipment.setNumber(command.number());
            equipment.setName(command.name());

            Division division = optionalDivision.get();
            equipment.setDivision(division);

            EquipmentType equipmentType = optionalEquipmentType.get();
            equipment.setEquipmentType(equipmentType);

            equipmentRepository.save(equipment);
        }
        return "redirect:/equipments/edit/{id}";
    }
}


