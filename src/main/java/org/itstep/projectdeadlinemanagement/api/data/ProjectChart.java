package org.itstep.projectdeadlinemanagement.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectChart {
    private TermDate project;
    private TermDate design;
    private TermDate technology;
    private TermDate materialContracts;
    private TermDate partProduction;
    private TermDate componentContracts;
    private TermDate assemblyProduction;

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
