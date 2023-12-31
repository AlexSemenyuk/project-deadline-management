package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itstep.projectdeadlinemanagement.command.ContractCommand;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.itstep.projectdeadlinemanagement.service.ProductionPlanService;
import org.itstep.projectdeadlinemanagement.service.TaskService;
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
    private final ProjectConditionRepository projectConditionRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final TaskService taskService;
    private final ProductionPlanService productionPlanService;

    @GetMapping("/{id}")
    public String findAll(@PathVariable Integer id, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> model.addAttribute("projectContract", project));
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

    @GetMapping(("finish/{id}"))
    String finish(@PathVariable Integer id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        optionalProject.ifPresent(project -> {
            Optional<ProjectStatus> optionalProjectStatus = projectStatusRepository.findById(2);
            if (optionalProjectStatus.isPresent()) {
                ProjectStatus projectStatus = optionalProjectStatus.get();

                project.setContractStatus(projectStatus);
                projectRepository.save(project);

                if (project.getProjectCondition().getName().equals("Production") &&
                    project.getDesignStatus().getName().equals("Finish") &&
                    project.getTechnologyStatus().getName().equals("Finish") &&
                    project.getContractStatus().getName().equals("Finish")){
                    List<Task> tasks = taskService.formTasks(id);
                    productionPlanService.formProductionPlans(tasks);
                }

            }


        });
        return "redirect:/";
    }

    @GetMapping(("{id}/delete/{contractId}"))
    String delete(@PathVariable Integer id, @PathVariable Integer contractId) {
        Optional<Contract> optionalContract = contractRepository.findById(contractId);
        optionalContract.ifPresent(contract -> contractRepository.deleteById(contractId));
        return "redirect:/contracts/project_contracts/{id}";
    }

    @GetMapping("/{id}/edit/{contractId}")
    public String findById(@PathVariable Integer id, @PathVariable Integer contractId, Model model) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        Optional<Contract> optionalContract = contractRepository.findById(contractId);
        if (optionalProject.isPresent() && optionalContract.isPresent()){
            Project project = optionalProject.get();
            model.addAttribute("project", project);
            Contract contract = optionalContract.get();
            model.addAttribute("contract", contract);
        }
        List<ContractType> contractTypes = contractTypeRepository.findAll();
        model.addAttribute("contractTypes", contractTypes);
        return "contract_edit";
    }

    @PostMapping("/{id}/edit/{contractId}")
    public String edit(@PathVariable Integer id, @PathVariable Integer contractId,  ContractCommand command) {

        Optional<Contract> optionalContract = contractRepository.findById(contractId);
        Optional<ContractType> optionalContractType = contractTypeRepository.findById(command.contractTypeId());
        if (optionalContract.isPresent() &&
                optionalContractType.isPresent() ){

            Contract contract = optionalContract.get();
            ContractType contractType = optionalContractType.get();

            contract.setNumber(command.number());
            contract.setName(command.name());
            contract.setStart(command.start());
            contract.setDeadline(command.deadline());
            contract.setContractType(contractType);
            contractRepository.save(contract);
        }
        return "redirect:/contracts/project_contracts/{id}/edit/{contractId}";
    }



}








