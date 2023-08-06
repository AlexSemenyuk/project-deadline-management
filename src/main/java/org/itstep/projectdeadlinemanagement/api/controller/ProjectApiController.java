package org.itstep.projectdeadlinemanagement.api.controller;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.api.data.ProjectChart;
import org.itstep.projectdeadlinemanagement.api.service.ProjectApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProjectApiController {
    private final ProjectApiService projectService;
    @PostMapping("/chart/{id}")
    ProjectChart chart (@PathVariable Integer id){
        ProjectChart projectChart = projectService.formProjectChart(id);
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
