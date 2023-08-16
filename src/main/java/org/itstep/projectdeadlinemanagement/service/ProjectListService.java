package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class ProjectListService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskConditionRepository taskConditionRepository;
    private final EquipmentRepository equipmentRepository;

    public List<AssemblyList> getAllAssemblyListsWithAmountOnProject(ProjectList projectList) {
        int count;
        List<AssemblyList> assemblyLists = projectList.getAssemblyLists();
        List<AssemblyList> rezultAssemblyLists = new CopyOnWriteArrayList<>();

        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                count = assemblyList.getAmount();
//                System.out.println("assemblyList1 = " + assemblyList.getAssembly().getNumber() + " - " +
//                        assemblyList.getAssembly().getName() + " x " + assemblyList.getAmount());
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                rezultAssemblyLists = extractAssemblyLists(assemblies, rezultAssemblyLists, count);

                AssemblyList newAssemblyList = new AssemblyList();
                newAssemblyList.setId(assemblyList.getId());
                newAssemblyList.setAssembly(assemblyList.getAssembly());
                newAssemblyList.setAmount(assemblyList.getAmount());
//                System.out.println("newAssemblyList1 = " + newAssemblyList.getAssembly().getNumber() + " - " +
//                                    newAssemblyList.getAssembly().getName() + " x " + newAssemblyList.getAmount());
                rezultAssemblyLists.add(assemblyList);
            }
        }
        rezultAssemblyLists = getUniqueAssemblyListWithAmountOnProject(rezultAssemblyLists);
        return rezultAssemblyLists;
    }

    private List<AssemblyList> extractAssemblyLists(List<AssemblyList> assemblyLists,
                                                    List<AssemblyList> rezultAssemblyLists,
                                                    int count) {
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                int countLocal = count * assemblyList.getAmount();
//                System.out.println("countLocal = " + countLocal);
//                System.out.println("assemblyList2 = " + assemblyList.getAssembly().getNumber() + " - " +
//                        assemblyList.getAssembly().getName() + " x " + assemblyList.getAmount());
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                if (!assemblies.isEmpty()) {
                    extractAssemblyLists(assemblies, rezultAssemblyLists, countLocal);
                }

                int amountTmp = assemblyList.getAmount();

                AssemblyList newAssemblyList = new AssemblyList();
                newAssemblyList.setId(assemblyList.getId());
                newAssemblyList.setAssembly(assemblyList.getAssembly());
                newAssemblyList.setAmount(amountTmp * count);

//                System.out.println("newAssemblyList2 = " + newAssemblyList.getAssembly().getNumber() + " - " +
//                        newAssemblyList.getAssembly().getName() + " x " + newAssemblyList.getAmount());
                rezultAssemblyLists.add(newAssemblyList);
            }
        }
        return rezultAssemblyLists;
    }

    public List<AssemblyList> getUniqueAssemblyListWithAmountOnProject(List<AssemblyList> allAssemblyLists) {
        List<Integer> uniqueAssemblyNumbers = new ArrayList<>();
        List<AssemblyList> resultAssemblyLists = new ArrayList<>();
//        System.out.println("Unique");

        for (AssemblyList assemblyList : allAssemblyLists) {
            Integer number = assemblyList.getAssembly().getNumber();
            int amount = assemblyList.getAmount();
//            System.out.println("amount = " + amount);
//            System.out.println("assemblyList1 = " + assemblyList.getAssembly().getNumber() + " - " +
//                    assemblyList.getAssembly().getName() + " x " + assemblyList.getAmount());

            if (!uniqueAssemblyNumbers.contains(number)) {      // Если есть в List<Integer> указанный элемент
                uniqueAssemblyNumbers.add(number);
                resultAssemblyLists.add(assemblyList);
//                System.out.println("assemblyList2 = " + assemblyList.getAssembly().getNumber() + " - " +
//                        assemblyList.getAssembly().getName() + " x " +
//                        assemblyList.getAmount());
            } else {
                for (AssemblyList resultAssemblyList : resultAssemblyLists) {
                    if (resultAssemblyList.getAssembly().getNumber().equals(number)) {
//                        System.out.println("resultAssemblyList1 = " + resultAssemblyList.getAssembly().getNumber() + " - " +
//                                resultAssemblyList.getAssembly().getName() + " x " + resultAssemblyList.getAmount());
                        int amountTmp = resultAssemblyList.getAmount();
                        resultAssemblyList.setAmount(amountTmp + amount);
//                        System.out.println("resultAssemblyList2 = " + resultAssemblyList.getAssembly().getNumber() + " - " +
//                                resultAssemblyList.getAssembly().getName() + " x " + resultAssemblyList.getAmount());
                        break;
                    }
                }
            }
        }
        return resultAssemblyLists;
    }


    public List<PartList> getAllPartListsWithAmountOnProject(ProjectList projectList) {
        int count;
        List<AssemblyList> assemblyLists = projectList.getAssemblyLists();
        List<PartList> rezultPartLists = new CopyOnWriteArrayList<>();
        if (!projectList.getPartLists().isEmpty()){
            for (PartList partList: projectList.getPartLists()){
//                System.out.println("partList1 = " + partList.getPart().getNumber() + " - " +
//                        partList.getPart().getName() + " x " +partList.getAmount());
                PartList newPartList = new PartList();
                newPartList.setId(partList.getId());
                newPartList.setPart(partList.getPart());
                newPartList.setAmount(partList.getAmount());
                rezultPartLists.add(newPartList);
            }
        }
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                Assembly assembly = assemblyList.getAssembly();
                count = assemblyList.getAmount();
                rezultPartLists = extractPartLists(assembly.getAssemblyListsEntry(), rezultPartLists, count);

                if (!assembly.getPartLists().isEmpty()){
                    for (PartList partList: assembly.getPartLists()){
                        int amountTmp = partList.getAmount();
//                        System.out.println("partList2-1 = " + partList.getPart().getNumber() + " - " +
//                                partList.getPart().getName() + " x " + partList.getAmount());
                        PartList newPartList = new PartList();
                        newPartList.setId(partList.getId());
                        newPartList.setPart(partList.getPart());
                        newPartList.setAmount(amountTmp * count);
//                        System.out.println("partList2-2 = " + newPartList.getPart().getNumber() + " - " +
//                                newPartList.getPart().getName() + " x " + newPartList.getAmount());
                        rezultPartLists.add(newPartList);
                    }
                }
            }
        }
        rezultPartLists = getUniquePartListWithAmountOnProject(rezultPartLists);
        return rezultPartLists;
    }

    private List<PartList> extractPartLists(List<AssemblyList> assemblyLists, List<PartList> rezultPartLists, int count) {
        int countLocal;
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                countLocal = count * assemblyList.getAmount();
                Assembly assembly = assemblyList.getAssembly();
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                if (!assembly.getAssemblyListsEntry().isEmpty()) {
                    extractPartLists(assemblies, rezultPartLists, countLocal);
                }

                if (!assembly.getPartLists().isEmpty()){
                    for (PartList partList: assembly.getPartLists()){
                        int amountTmp = partList.getAmount();
//                        System.out.println("partList3-1 = " + partList.getPart().getNumber() + " - " +
//                                partList.getPart().getName() + " x " +partList.getAmount());
                        PartList newPartList = new PartList();
                        newPartList.setId(partList.getId());
                        newPartList.setPart(partList.getPart());
                        newPartList.setAmount(amountTmp * countLocal);

//                        System.out.println("partList3-2 = " + newPartList.getPart().getNumber() + " - " +
//                                newPartList.getPart().getName() + " x " +newPartList.getAmount());
                        rezultPartLists.add(newPartList);
                    }
                }
            }
        }
        return rezultPartLists;
    }

    public List<PartList> getUniquePartListWithAmountOnProject(List<PartList> allPartLists) {
//        System.out.println("Unique");
        List<Integer> uniquePartNumbers = new ArrayList<>();
        List<PartList> resultPartLists = new ArrayList<>();

        for (PartList partList : allPartLists) {
            Integer number = partList.getPart().getNumber();
            int amount = partList.getAmount();
//            System.out.println("partList5-1 = " + partList.getPart().getNumber() + " - " +
//                    partList.getPart().getName() + " x " +partList.getAmount());

            if (!uniquePartNumbers.contains(number)) {      // Если есть в List<Integer> указанный элемент
                uniquePartNumbers.add(number);
                resultPartLists.add(partList);
//                System.out.println("partList5-2 = " + partList.getPart().getNumber() + " - " +
//                        partList.getPart().getName() + " x " +partList.getAmount());
            } else {
                for (PartList resultPartList : resultPartLists) {
                    if (resultPartList.getPart().getNumber().equals(number)) {
                        int amountTmp = resultPartList.getAmount();
                        resultPartList.setAmount(amountTmp + amount);
//                        System.out.println("partList5-3 = " + partList.getPart().getNumber() + " - " +
//                                partList.getPart().getName() + " x " +partList.getAmount());
                        break;
                    }
                }
            }
        }
        return resultPartLists;
    }
}




