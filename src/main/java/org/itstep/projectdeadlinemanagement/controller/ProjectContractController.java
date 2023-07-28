package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.ContractCommand;
import org.itstep.projectdeadlinemanagement.command.DivisionTypeCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.ContractRepository;
import org.itstep.projectdeadlinemanagement.repository.ContractTypeRepository;
import org.itstep.projectdeadlinemanagement.repository.DivisionTypeRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("contracts/project_contracts")
@Slf4j
public class ProjectContractController {
    private final ContractRepository contractRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final ProjectRepository projectRepository;
    @GetMapping("/{id}")
    public String findAll(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            model.addAttribute("projectContract", project);
        });
        List<ContractType> contractTypes = contractTypeRepository.findAll();
        model.addAttribute("contractTypes", contractTypes);
        return "project_contracts";
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

    @GetMapping(("{id}/delete/{contractId}"))
    String delete(@PathVariable Integer id, @PathVariable Integer contractId) {
        Optional<Contract> optionalContract = contractRepository.findById(contractId);
        optionalContract.ifPresent(contract -> contractRepository.deleteById(contractId));
        return "redirect:/contracts/project_contracts/{id}";
    }
}








