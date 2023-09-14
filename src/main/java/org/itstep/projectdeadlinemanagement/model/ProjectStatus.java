package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.ProjectStatusCommand;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "project_status")
@NoArgsConstructor
@ToString(exclude = {"designProjects", "technologyProjects", "contractProjects"})
@EqualsAndHashCode(exclude = {"designProjects", "technologyProjects", "contractProjects"})
public class ProjectStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "designStatus")
    private List<Project> designProjects = new ArrayList<>();

    @OneToMany(mappedBy = "technologyStatus")
    private List<Project> technologyProjects = new ArrayList<>();

    @OneToMany(mappedBy = "contractStatus")
    private List<Project> contractProjects = new ArrayList<>();

    public ProjectStatus(String name) {
        this.name = name;
    }

    public static ProjectStatus fromCommand(ProjectStatusCommand command) {
        return new ProjectStatus(command.name());
    }
}



