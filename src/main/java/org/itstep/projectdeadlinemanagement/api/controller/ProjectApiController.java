package org.itstep.projectdeadlinemanagement.api.controller;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.api.data.ProjectChart;
import org.itstep.projectdeadlinemanagement.api.data.ProjectRequest;
import org.itstep.projectdeadlinemanagement.api.service.ProjectService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProjectApiController {
    private final ProjectService projectService;
    @PostMapping("/chart/{id}")
    ProjectChart chart (@PathVariable Integer id){
        System.out.println("id = " + id);
        ProjectChart projectChart = projectService.formProjectChart(id);
        if (projectChart != null){
            System.out.println("projectChart.toString() = " + projectChart.toString());
        }
        return projectChart;
    }

//    @PostMapping("/chart/{id}")
//    ProjectChart chart (@PathVariable Integer id, , @RequestBody ProjectRequest request){
////        ProjectChart projectChart = projectService.formProjectChart(id);
//        System.out.println("id = " + id);
//
//        return "hello";
//    }
}
