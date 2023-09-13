package org.itstep.projectdeadlinemanagement.api.controller;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.api.data.ProjectChart;
import org.itstep.projectdeadlinemanagement.api.service.ProjectApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProjectApiController {
    private final ProjectApiService projectApiService;
    @PostMapping("/chart/{id}")
    ProjectChart chart (@PathVariable Integer id){
        return projectApiService.formProjectChart(id);
    }

}
