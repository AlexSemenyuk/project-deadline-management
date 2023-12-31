package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartOrAssemblyService {
    private final ProjectRepository projectRepository;

    public Project findProject (int projectNumber){
        Project projectTmp = null;
        List<Project> projects = projectRepository.findAll();
        if (!projects.isEmpty()){
            for (Project project: projects){
                if (project.getNumber() == projectNumber){
                    projectTmp = project;
                    break;
                }
            }
        }
        return projectTmp;
    }

    public List<Task> findTasks (List<Task> taskList, int partOrAssemblyNumber){
        List<Task> tasks = new CopyOnWriteArrayList<>();
        for (Task t: taskList){
            if (t.getPartOrAssemblyNumber() == partOrAssemblyNumber && t.getLotNumber() == 1){
                tasks.add(t);
                System.out.println(t.getId() + ", " + t.getPartOrAssemblyNumber() + ", " + t.getLotNumber());
            }
        }
        if (!tasks.isEmpty()){
            tasks = tasks.stream()
                    .sorted(Comparator.comparing(Task::getTermNumber))
                    .collect(Collectors.toList());
        }
        return tasks;
    }

}
