package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.EquipmentRepository;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskConditionRepository;
import org.itstep.projectdeadlinemanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class ProjectListService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskConditionRepository taskConditionRepository;
    private final EquipmentRepository equipmentRepository;


    public List<AssemblyList> getAllAssemblyLists(ProjectList projectList) {
        List<AssemblyList> assemblyLists = projectList.getAssemblyLists();
        List<AssemblyList> rezultAssemblyLists = new CopyOnWriteArrayList<>();
        int count = 0;
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                rezultAssemblyLists = extractAssemblyLists(assemblies, rezultAssemblyLists);
                rezultAssemblyLists.add(assemblyList);
//                System.out.println("assemblyList.getAssembly().getName() = " + assemblyList.getAssembly().getName());
//                if (!assemblyList.getAssembly().getPartLists().isEmpty()){
//                    assemblyList.getAssembly().getPartLists().forEach(partList -> {
//                        System.out.println(partList.getPart().getNumber() + "-" + partList.getPart().getName());
//                    });
//                }
            }
        }
        return rezultAssemblyLists;
    }

    private List<AssemblyList> extractAssemblyLists(List<AssemblyList> assemblyLists, List<AssemblyList> rezultAssemblyLists) {
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                if (!assemblies.isEmpty()) {
                    extractAssemblyLists(assemblies, rezultAssemblyLists);
                }
                rezultAssemblyLists.add(assemblyList);

//                System.out.println("assemblyList.getAssembly().getName() = " + assemblyList.getAssembly().getName());
//                if (!assemblyList.getAssembly().getPartLists().isEmpty()){
//                    assemblyList.getAssembly().getPartLists().forEach(partList -> {
//                        System.out.println(partList.getPart().getNumber() + "-" + partList.getPart().getName());
//                    });
//                }
            }
        }
        return rezultAssemblyLists;
    }

    public List<PartList> getAllPartLists(ProjectList projectList) {
        List<AssemblyList> assemblyLists = projectList.getAssemblyLists();
        List<PartList> rezultPartLists = new CopyOnWriteArrayList<>();
        if (!projectList.getPartLists().isEmpty()){
            for (PartList partList: projectList.getPartLists()){
                rezultPartLists.add(partList);
            }
        }

        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                Assembly assembly = assemblyList.getAssembly();

                rezultPartLists = extractPartLists(assembly.getAssemblyListsEntry(), rezultPartLists);

                if (!assembly.getPartLists().isEmpty()){
                    for (PartList partList: assembly.getPartLists()){
                        rezultPartLists.add(partList);
                    }
                }
            }
        }
        return rezultPartLists;
    }

    private List<PartList> extractPartLists(List<AssemblyList> assemblyLists, List<PartList> rezultPartLists) {
        if (!assemblyLists.isEmpty()) {
            for (AssemblyList assemblyList : assemblyLists) {
                Assembly assembly = assemblyList.getAssembly();
                List<AssemblyList> assemblies = assemblyList.getAssembly().getAssemblyListsEntry();
                if (!assembly.getAssemblyListsEntry().isEmpty()) {
                    extractPartLists(assemblies, rezultPartLists);
                }

                if (!assembly.getPartLists().isEmpty()){
                    for (PartList partList: assembly.getPartLists()){
                        rezultPartLists.add(partList);
                    }
                }
            }
        }
        return rezultPartLists;
    }
}




