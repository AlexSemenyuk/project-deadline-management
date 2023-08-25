package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.ContractTypeCommand;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Table(name = "contract_types")
@NoArgsConstructor
@ToString(exclude = "contracts")
@EqualsAndHashCode(exclude = "contracts")
public class ContractType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "contractType")
    private List<Contract> contracts = new ArrayList<>();

    public ContractType(String name) {
        this.name = name;
    }

    public static ContractType fromCommand(ContractTypeCommand command) {
        return new ContractType(command.name());
    }
}
