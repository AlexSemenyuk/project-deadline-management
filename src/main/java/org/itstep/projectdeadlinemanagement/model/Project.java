package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ProjectCommand;

import java.time.LocalDateTime;
import java.util.Date;

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

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime deadline;


    public Project(Integer number, String name, LocalDateTime start, LocalDateTime deadline) {
        this.number = number;
        this.name = name;
        this.start = start;
        this.deadline = deadline;
    }

    public void setCustomer (Customer customer) {
        customer.getProjects().add(this);
        this.customer = customer;
    }

    public static Project fromCommand(ProjectCommand command) {
        return new Project(command.number(),
                command.name(),
                command.start(),
                command.deadline());
    }
}



