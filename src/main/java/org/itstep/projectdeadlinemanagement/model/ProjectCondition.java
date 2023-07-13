package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.CustomerCommand;
import org.itstep.projectdeadlinemanagement.command.ProjectConditionCommand;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Table(name = "conditions")
@NoArgsConstructor
@ToString(exclude = "projects")
@EqualsAndHashCode(exclude = "projects")
public class ProjectCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "projectCondition")
    private List<Project> projects = new ArrayList<>();

    public ProjectCondition(String name) {
        this.name = name;
    }

    public static ProjectCondition fromCommand(ProjectConditionCommand command) {
        return new ProjectCondition(command.name());
    }

}


