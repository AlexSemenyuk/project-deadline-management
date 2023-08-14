package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.PartListCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "part_lists")
@NoArgsConstructor
public class PartList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Part part;

    @Column(nullable = false)
    Integer amount;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Assembly> assemblies = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProjectList> projectLists = new ArrayList<>();

    public PartList(Integer amount) {
        this.amount = amount;
    }

    public void setPart (Part part) {
        part.getPartLists().add(this);
        this.part = part;
    }

    public void addAssembly(Assembly assembly) {
        assembly.getPartLists().add(this);
        assemblies.add(assembly);
    }

    public void addProjectList(ProjectList projectList) {
        projectList.getPartLists().add(this);
        projectLists.add(projectList);
    }

    public static PartList fromCommand(PartListCommand command) {
        return new PartList(command.amount());
    }
}

