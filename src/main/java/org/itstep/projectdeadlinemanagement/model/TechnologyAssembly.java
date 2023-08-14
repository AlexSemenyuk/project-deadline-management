package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.TechnologyAssemblyCommand;

@Entity
@Data
@Table(name = "technology_assemblies")
@NoArgsConstructor
@ToString(exclude = {"assembly", "equipment"})
@EqualsAndHashCode(exclude = {"assembly", "equipment"})
public class TechnologyAssembly {
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


    public TechnologyAssembly(Integer number, Integer operationTime) {
        this.number = number;
        this.operationTime = operationTime;
    }

    public void setAssembly (Assembly assembly) {
        assembly.getTechnologyAssemblies().add(this);
        this.assembly = assembly;
    }


    public void setEquipment (Equipment equipment) {
        equipment.getTechnologyAssemblies().add(this);
        this.equipment = equipment;
    }

    public static TechnologyAssembly fromCommand(TechnologyAssemblyCommand command) {
        return new TechnologyAssembly(command.number(), command.operationTime());
    }
}



