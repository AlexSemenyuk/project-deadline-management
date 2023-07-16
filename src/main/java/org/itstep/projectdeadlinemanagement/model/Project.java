package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProjectList> projectLists = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ProjectCondition projectCondition;

    //    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private List<Task> tasks = new ArrayList<>();
    @OneToMany(mappedBy = "project")
    private List<Task> tasks = new ArrayList<>();

    public Project(Integer number, LocalDateTime start, LocalDateTime deadline) {
        this.number = number;
        this.start = start;
        this.deadline = deadline;
    }

    public void setCustomer(Customer customer) {
        customer.getProjects().add(this);
        this.customer = customer;
    }

    public void setProjectCondition(ProjectCondition projectCondition) {
        projectCondition.getProjects().add(this);
        this.projectCondition = projectCondition;
    }

    public void addProjectList(ProjectList projectList) {
        projectList.getProjects().add(this);
        projectLists.add(projectList);
    }

    //    public void addTask(Task task) {
//        task.getProjects().add(this);
//        tasks.add(task);
//    }


    public static Project fromCommand(ProjectCommand command) {
        return new Project(command.number(),
                command.start(),
                command.deadline());
    }
}



