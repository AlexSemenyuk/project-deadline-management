package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ContractCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping("contracts/project_contracts/contract_create")
@RequiredArgsConstructor
@Slf4j
public class ContractCreateController {
    private final ProjectRepository projectRepository;
    private final ContractRepository contractRepository;
    private final ContractTypeRepository contractTypeRepository;



    @GetMapping("/{id}")
    public String home(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("project", project);
        });
        List<ContractType> contractTypes = contractTypeRepository.findAll();
        model.addAttribute("contractTypes", contractTypes);
        return "contract_create";
    }

    @PostMapping("/{id}")
    public String create(@PathVariable Integer id, ContractCommand command) {
        log.info("ContractCommand {}", command);
        Optional<ContractType> optionalContractType = contractTypeRepository.findById(command.contractTypeId());
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalContractType.isPresent() && optionalProject.isPresent()){
            ContractType contractType = optionalContractType.get();
            Project project = optionalProject.get();
            Contract contract = Contract.fromCommand(command);
            contract.setContractType(contractType);
            contract.setProject(project);
            contractRepository.save(contract);
        }
        return "redirect:/contracts/project_contracts/{id}";
    }



}

