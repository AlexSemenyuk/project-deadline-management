package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.EquipmentCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "equipments")
@NoArgsConstructor
@ToString(exclude = {"partTerms", "assemblyTerms", "tasks"})
@EqualsAndHashCode(exclude = {"partTerms", "assemblyTerms", "tasks"})
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Division division;

    @OneToMany(mappedBy = "equipment")
    private List<TechnologyPart> TechnologyParts = new ArrayList<>();

    @OneToMany(mappedBy = "equipment")
    private List<TechnologyAssembly> TechnologyAssemblies = new ArrayList<>();

    @OneToMany(mappedBy = "equipment")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "equipment")
    private List<ProductionPlan> productionPlans = new ArrayList<>();

    public Equipment(Integer number, String name) {
        this.number = number;
        this.name = name;
    }



    public void setDivision (Division division) {
        division.getEquipments().add(this);
        this.division = division;
    }

    public static Equipment fromCommand(EquipmentCommand command) {
        return new Equipment (command.number(), command.name());
    }
}

