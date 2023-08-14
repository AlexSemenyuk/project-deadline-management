package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ProjectListCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "project_lists")
public class ProjectList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "projectList")
    private List<Project> projects = new ArrayList<>();

    @ManyToMany(mappedBy = "projectLists")
    private List<AssemblyList> assemblyLists = new ArrayList<>();

    @ManyToMany(mappedBy = "projectLists")
    private List<PartList> partLists = new ArrayList<>();

    public ProjectList() {
    }

    public void addAssemblyList(AssemblyList assemblyList) {
        assemblyList.getProjectLists().add(this);
        assemblyLists.add(assemblyList);
    }
    public void addPartList(PartList partList) {
        partList.getProjectLists().add(this);
        partLists.add(partList);
    }

}
