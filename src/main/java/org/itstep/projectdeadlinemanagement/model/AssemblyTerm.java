package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.AssemblyTermCommand;
import org.itstep.projectdeadlinemanagement.command.PartTermCommand;
@Entity
@Data
@Table(name = "assembly_terms")
@NoArgsConstructor
@ToString(exclude = {"assembly", "equipment"})
@EqualsAndHashCode(exclude = {"assembly", "equipment"})
public class AssemblyTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer operationTime;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Assembly assembly;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Equipment equipment;


    public AssemblyTerm (Integer number, Integer operationTime) {
        this.number = number;
        this.operationTime = operationTime;
    }

    public void setAssembly (Assembly assembly) {
        assembly.getAssemblyTerms().add(this);
        this.assembly = assembly;
    }


    public void setEquipment (Equipment equipment) {
        equipment.getAssemblyTerms().add(this);
        this.equipment = equipment;
    }



    public static AssemblyTerm fromCommand(AssemblyTermCommand command) {
        return new AssemblyTerm(command.number(), command.operationTime());
    }
}



