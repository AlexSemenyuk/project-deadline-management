package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ContractCommand;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contracts")
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ContractType contractType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Project project;


    public Contract(String number, String name, LocalDateTime start, LocalDateTime deadline) {
        this.number = number;
        this.name = name;
        this.start = start;
        this.deadline = deadline;
    }

    public void setContractType (ContractType contractType) {
        contractType.getContracts().add(this);
        this.contractType = contractType;
    }

    public void setProject (Project project) {
        project.getContracts().add(this);
        this.project = project;
    }

    public static Contract fromCommand(ContractCommand command) {
        return new Contract (
                command.number(),
                command.name(),
                command.start().withHour(0).withMinute(0),
                command.deadline().withHour(0).withMinute(0)
        );
    }

}


