package org.itstep.projectdeadlinemanagement.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
@Data
@AllArgsConstructor
public class ProjectChart {
    private Term project;
    private Term design;
    private Term technology;
    private Term contracts;
    private Term production;

//    public Term getProject() {
//        return project;
//    }
//
//    public void setProject(Term project) {
//        this.project = project;
//    }
//
//    public Term getDesign() {
//        return design;
//    }
//
//    public void setDesign(Term design) {
//        this.design = design;
//    }
//
//    public Term getTechnology() {
//        return technology;
//    }
//
//    public void setTechnology(Term technology) {
//        this.technology = technology;
//    }
//
//    public Term getContracts() {
//        return contracts;
//    }
//
//    public void setContracts(Term contracts) {
//        this.contracts = contracts;
//    }
//
//    public Term getProduction() {
//        return production;
//    }
//
//    public void setProduction(Term production) {
//        this.production = production;
//    }
}
