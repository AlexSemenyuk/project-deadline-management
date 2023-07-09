package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.CustomerCommand;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "customers")
@NoArgsConstructor
@ToString(exclude = "projects")
@EqualsAndHashCode(exclude = "projects")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "customer")
    private List<Project> projects = new ArrayList<>();

    public Customer(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public static Customer fromCommand(CustomerCommand command) {
        return new Customer(command.name(), command.address());
    }
}

