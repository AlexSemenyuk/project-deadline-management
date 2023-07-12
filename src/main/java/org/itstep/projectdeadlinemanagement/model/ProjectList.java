package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ProjectListCommand;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class ProjectList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Assembly assembly;

    @Column(nullable = false)
    Integer amount;

    public ProjectList(Integer amount) {
        this.amount = amount;
    }

    public void setAssembly (Assembly assembly) {
        assembly.getOrders().add(this);
        this.assembly = assembly;
    }

    public static ProjectList fromCommand(ProjectListCommand command) {
        return new ProjectList(command.amount());
    }
}
