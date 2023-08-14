package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "projects")
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ProjectList projectList;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime start;

    private int designTerm = 0;

    private int technologyTerm = 0;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ProjectCondition projectCondition;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Contract> contracts = new ArrayList<>();

    public Project(Integer number, LocalDateTime start, LocalDateTime deadline) {
        this.number = number;
        this.start = start;
        this.deadline = deadline;
    }

    public void setCustomer(Customer customer) {
        customer.getProjects().add(this);
        this.customer = customer;
    }

    public void setProjectList(ProjectList projectList) {
        projectList.getProjects().add(this);
        this.projectList = projectList;
    }

    public void setProjectCondition(ProjectCondition projectCondition) {
        projectCondition.getProjects().add(this);
        this.projectCondition = projectCondition;
    }


    public static Project fromCommand(ProjectCommand command) {
        return new Project(command.number(),
                command.start(),
                command.deadline());
    }
}



