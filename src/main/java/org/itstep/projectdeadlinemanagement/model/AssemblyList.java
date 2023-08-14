package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.AssemblyListCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "assembly_lists")
@NoArgsConstructor
public class AssemblyList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Assembly assembly;

    @Column(nullable = false)
    Integer amount;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Assembly> assemblies = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProjectList> projectLists = new ArrayList<>();

    public AssemblyList(Integer amount) {
        this.amount = amount;
    }

    public void setAssembly (Assembly assembly) {
        assembly.getAssemblyLists().add(this);
        this.assembly = assembly;
    }

    public void addAssemblyListEntry(Assembly assemblyEntry) {
        assemblyEntry.getAssemblyListsEntry().add(this);
        assemblies.add(assemblyEntry);
    }

    public void addProjectList(ProjectList projectList) {
        projectList.getAssemblyLists().add(this);
        projectLists.add(projectList);
    }


    public static AssemblyList fromCommand(AssemblyListCommand command) {
        return new AssemblyList(command.amount());
    }
}
