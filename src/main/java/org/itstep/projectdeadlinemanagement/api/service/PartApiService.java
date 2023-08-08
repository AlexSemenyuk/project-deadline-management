package org.itstep.projectdeadlinemanagement.api.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.api.data.PartChart;
import org.itstep.projectdeadlinemanagement.api.data.TermHours;
import org.itstep.projectdeadlinemanagement.model.Project;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.service.PartService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class PartApiService {
    private final PartService partService;

    public PartChart formPartChart(String projectAndPart) {
        String [] tmp = projectAndPart.split(":");
        int projectNumber = Integer.parseInt(tmp[0]);
        int partNumber = Integer.parseInt(tmp[1]);
//        System.out.println("projectNumber = " + projectNumber);
//        System.out.println("partNumber = " + partNumber);

        Project project = partService.findProject(projectNumber);
        List<Task> tasks = partService.findTasks(project.getTasks(), partNumber);

        List<TermHours> termHoursList = new CopyOnWriteArrayList<>();
        List<Integer> termNumberList = new CopyOnWriteArrayList<>();
        int projectNumberTMP = 0;
        int partNumberTMP = 0;
        String partNameTMP = "";
        if (!tasks.isEmpty()){
            projectNumberTMP = tasks.get(0).getProjectNumber();
            partNumberTMP = tasks.get(0).getPartNumber();
            partNameTMP = tasks.get(0).getPartName();
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

        PartChart partChart = new PartChart(projectNumberTMP, partNumberTMP, partNameTMP, termNumberList, termHoursList);
//        private int termNumber;
//        private int start;
//        private int deadline;
//        private String projectNumber;
//        private String partNumber;
//        private String partName;
//        List<TermHours> termHoursList;
//        System.out.println("partChart = " + partChart);
        return partChart;
    }

}


