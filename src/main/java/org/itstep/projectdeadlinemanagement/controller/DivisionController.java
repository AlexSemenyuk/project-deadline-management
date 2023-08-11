package org.itstep.projectdeadlinemanagement.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.model.Division;
import org.itstep.projectdeadlinemanagement.model.DivisionType;
import org.itstep.projectdeadlinemanagement.repository.DivisionRepository;
import org.itstep.projectdeadlinemanagement.repository.DivisionTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/divisions")
public class DivisionController {

    private final DivisionRepository divisionRepository;
    private final DivisionTypeRepository divisionTypeRepository;

    @GetMapping
    public String home(Model model) {
        List<Division> divisions = divisionRepository.findAll();
        model.addAttribute("divisions", divisions);
        model.addAttribute("types", divisionTypeRepository.findAll());
        return "divisions";
    }

    @PostMapping
    public String create(DivisionCommand command) {
        log.info("DivisionCommand {}", command);
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(command.divisionTypeId());
        optionalDivisionType.ifPresent(divisionType -> {
            Division division = Division.fromCommand(command);
            division.setDivisionType(divisionType);
            divisionRepository.save(division);
        });
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
        List<DivisionType> divisionTypes = divisionTypeRepository.findAll();
        model.addAttribute("types", divisionTypes);
        if (optionalDivision.isPresent()){
            Division division = optionalDivision.get();
            model.addAttribute("division", division);
            model.addAttribute("divisionTypeId", division.getDivisionType().getId());
        }
        return "division_edit";
    }

    @PostMapping("edit/{id}")
    public String edit(@PathVariable Integer id, DivisionCommand command) {
        Optional<Division> optionalDivision = divisionRepository.findById(id);
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(command.divisionTypeId());
        if (optionalDivision.isPresent() && optionalDivisionType.isPresent()){

            Division division = optionalDivision.get();

            division.setName(command.name());

            DivisionType divisionType = optionalDivisionType.get();
            division.setDivisionType(divisionType);

            divisionRepository.save(division);
        }
        return "redirect:/divisions/edit/{id}";
    }

}
