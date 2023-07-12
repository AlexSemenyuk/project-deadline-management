package org.itstep.projectdeadlinemanagement;

import lombok.RequiredArgsConstructor;

import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;
import org.itstep.projectdeadlinemanagement.controller.DivisionController;
import org.itstep.projectdeadlinemanagement.model.Assembly;
import org.itstep.projectdeadlinemanagement.model.Division;
import org.itstep.projectdeadlinemanagement.model.DivisionType;
import org.itstep.projectdeadlinemanagement.model.Part;
import org.itstep.projectdeadlinemanagement.repository.AssemblyRepository;
import org.itstep.projectdeadlinemanagement.repository.DivisionRepository;
import org.itstep.projectdeadlinemanagement.repository.DivisionTypeRepository;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
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

    @Override
    public void run(String... args) throws Exception {
        // 1. Деление дивизионов на подготовка (формирование проекта) / производство (изготовление проекта)
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


        // 3. Деление дивизионов на подготовка (формирование проекта) / производство (изготовление проекта)
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

        List<Integer> partList = new ArrayList<>();
        partList.add(1);
        partList.add(2);
        partList.add(3);
        partList.add(4);
        partList.add(5);
        partList.add(6);
        partList.add(7);
        partList.add(8);


//        AssemblyCommand assemblyCommand = new AssemblyCommand(1, "Футорка", partList);
//        List<Part> optionalParts = partRepository.findAllById(assemblyCommand.partsIds());
//
////        if (optionalParts != null){
//        Assembly assembly = Assembly.fromCommand(assemblyCommand);
//        List<Part> parts = partRepository.findAllById(assemblyCommand.partsIds());
//        parts.forEach(assembly::addPart);
////        }
//        assemblyReposutory.save(assembly);
    }
}

