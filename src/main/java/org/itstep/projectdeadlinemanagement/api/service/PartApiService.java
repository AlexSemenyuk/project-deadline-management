package org.itstep.projectdeadlinemanagement.api.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.api.data.PartChart;
import org.itstep.projectdeadlinemanagement.api.data.TermHours;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.service.PartOrAssemblyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartApiService {
    private final PartOrAssemblyService partService;

    public PartChart formPartChart(String projectAndPart) {
        String [] tmp = projectAndPart.split(":");
        int projectNumber = Integer.parseInt(tmp[0]);
        int partNumber = Integer.parseInt(tmp[1]);
//        System.out.println("projectNumber = " + projectNumber);
//        System.out.println("partNumber = " + partNumber);

        Project project = partService.findProject(projectNumber);
        List<Task> tasks = partService.findTasks(project.getTasks(), partNumber);

        List<TermHours> termHoursList = new ArrayList<>();
        List<Integer> termNumberList = new ArrayList<>();
        int projectNumberTMP = 0;
        int partNumberTMP = 0;
        String partNameTMP = "";
        if (!tasks.isEmpty()){
            projectNumberTMP = tasks.get(0).getProjectNumber();
            partNumberTMP = tasks.get(0).getPartOrAssemblyNumber();
            partNameTMP = tasks.get(0).getPartOrAssemblyName();
            int deadlineTmp = 0;
            int start;
            int deadline;
            for (Task task: tasks){
                if (deadlineTmp == 0){
                    start = 0;
                } else {
                    start = deadlineTmp;
                }
                deadline = start + task.getOperationTime();
                deadlineTmp = deadline;
                TermHours termHours = new TermHours(start, deadline);
                termHoursList.add(termHours);
                termNumberList.add(task.getTermNumber());
            }
        }

        return new PartChart(projectNumberTMP, partNumberTMP, partNameTMP, termNumberList, termHoursList);
    }

}


