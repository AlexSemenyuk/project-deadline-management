package org.itstep.projectdeadlinemanagement.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.model.Division;
import org.itstep.projectdeadlinemanagement.model.EquipmentType;
import org.itstep.projectdeadlinemanagement.repository.DivisionRepository;
import org.itstep.projectdeadlinemanagement.repository.EquipmentTypeRepository;
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
@RequestMapping("/divisions")
public class DivisionController {

    private final DivisionRepository divisionRepository;

    @GetMapping
    public String home(Model model) {
        List<Division> divisions = divisionRepository.findAll();
        model.addAttribute("divisions", divisions);
        return "divisions";
    }

    @PostMapping
    public String create(DivisionCommand command) {
        log.info("DivisionCommand {}", command);
            Division division = Division.fromCommand(command);
            divisionRepository.save(division);
        return "redirect:/divisions";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Division> optionalDivision = divisionRepository.findById(id);
        optionalDivision.ifPresent(division -> divisionRepository.deleteById(id));
        return "redirect:/divisions";
    }

    @GetMapping("edit/{id}")
    public String findById(@PathVariable Integer id, Model model) {
        Optional<Division> optionalDivision = divisionRepository.findById(id);
        if (optionalDivision.isPresent()){
            Division division = optionalDivision.get();
            model.addAttribute("division", division);
        }
        return "division_edit";
    }

    @PostMapping("edit/{id}")
    public String edit(@PathVariable Integer id, DivisionCommand command) {
        Optional<Division> optionalDivision = divisionRepository.findById(id);
        if (optionalDivision.isPresent() ){
            Division division = optionalDivision.get();
            division.setName(command.name());
            divisionRepository.save(division);
        }
        return "redirect:/divisions/edit/{id}";
    }

}
