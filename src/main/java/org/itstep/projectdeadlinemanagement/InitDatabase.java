package org.itstep.projectdeadlinemanagement;

import lombok.RequiredArgsConstructor;

import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.controller.DivisionController;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitDatabase implements CommandLineRunner {
    private final DivisionTypeRepository divisionTypeRepository;
    private final DivisionRepository divisionRepository;
    private final PartRepository partRepository;
    private final AssemblyRepository assemblyRepository;
    private final ProjectConditionRepository projectConditionRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Тип дивизиона (подготовка / производство)
        DivisionType preparation = new DivisionType("Preparation");
        DivisionType production = new DivisionType("Production");
        divisionTypeRepository.save(preparation);
        divisionTypeRepository.save(production);

        // 2. Создание дивизионов (конструкторский отдел, технологический отдел, мх-1 (механо-сборочный цех №1), мх-2, мх-3)
//        Division division = new Division(1, "Design / Archive");
//        preparation.setId(1);
//        division.setDivisionType(preparation);
//        divisionRepository.save(division);

        DivisionCommand command = new DivisionCommand(1, "Design / Archive", 1);
        Optional<DivisionType> optionalDivisionType = divisionTypeRepository.findById(command.divisionTypeId());
        optionalDivisionType.ifPresent(divisionType -> {
            Division division = Division.fromCommand(command);
            divisionType.setDivisions(new ArrayList<>());
            division.setDivisionType(divisionType);
//            divisionRepository.save(division);
        });

//        Division technologistDivision = new Division(2, "Technologist");
//        Division shopOneDivision = new Division(3, "Mechanical Shop №1");
//        Division shopTwoDivision = new Division(4, "Mechanical Shop №2");
//        Division shopThreeDivision = new Division(5, "Mechanical Shop №2");


        // 3. Создание деталей
        Part frame = new Part(1, "Корпус");
        Part cover1 = new Part(2, "Крышка");
        Part cover2 = new Part(3, "Крышка");
        Part cover3 = new Part(4, "Крышка");
        Part cover4 = new Part(5, "Крышка");
        Part cover5 = new Part(6, "Крышка");
        Part bolt1 = new Part(7, "<Болт>");
        Part bolt2 = new Part(8, "<Болт>");

        partRepository.save(frame);
        partRepository.save(cover1);
        partRepository.save(cover2);
        partRepository.save(cover3);
        partRepository.save(cover4);
        partRepository.save(cover5);
        partRepository.save(bolt1);
        partRepository.save(bolt2);

        // 4. Создание состояний проекта (Design, Technology, Production, Archive)
        ProjectCondition projectCondition1 = new ProjectCondition("Design");
        ProjectCondition projectCondition2 = new ProjectCondition("Technology");
        ProjectCondition projectCondition3 = new ProjectCondition("Production");
        ProjectCondition projectCondition4 = new ProjectCondition("Archive");

        projectConditionRepository.save(projectCondition1);
        projectConditionRepository.save(projectCondition2);
        projectConditionRepository.save(projectCondition3);
        projectConditionRepository.save(projectCondition4);

        // 4. Создание Зыказчиков / Customer
        Customer customer1 = new Customer("Запорожсталь", "Запорожье");
        Customer customer2 = new Customer("Арселор Миттаол Кривой Рог", "Кривой Рог");

        customerRepository.save(customer1);
        customerRepository.save(customer2);


    }
}

