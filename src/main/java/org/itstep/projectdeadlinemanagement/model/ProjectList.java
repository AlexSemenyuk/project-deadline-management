package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ProjectListCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class ProjectList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private Assembly assembly;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Part part;

    @Column(nullable = false)
    Integer amount;

    @ManyToMany(mappedBy = "projectLists")
    private List<Project> projects = new ArrayList<>();

    public ProjectList(Integer amount) {
        this.amount = amount;
    }

    public void setPart (Part part) {
        part.getProjectLists().add(this);
        this.part = part;
    }
//    public void setAssembly (Assembly assembly) {
//        assembly.getProjectLists().add(this);
//        this.assembly = assembly;
//    }

    public static ProjectList fromCommand(ProjectListCommand command) {
        return new ProjectList(command.amount());
    }
}
