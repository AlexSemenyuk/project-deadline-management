package org.itstep.projectdeadlinemanagement.api.controller;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.api.data.AssemblyChart;
import org.itstep.projectdeadlinemanagement.api.data.PartChart;
import org.itstep.projectdeadlinemanagement.api.service.AssemblyApiService;
import org.itstep.projectdeadlinemanagement.api.service.PartApiService;
import org.itstep.projectdeadlinemanagement.service.PartOrAssemblyService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AssemblyApiController {
    private final AssemblyApiService assemblyApiService;
    private final PartOrAssemblyService partService;
    @PostMapping("/chart_assembly/{id}")
    AssemblyChart chart (@PathVariable String id){
//        System.out.println("id = " + id);
        return assemblyApiService.formAssemblyChart(id);
    }
}

