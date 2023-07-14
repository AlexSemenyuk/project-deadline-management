package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.EquipmentCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "equipments")
@NoArgsConstructor
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
    private List<TermPart> partTerms = new ArrayList<>();

    @OneToMany(mappedBy = "equipment")
    private List<AssemblyTerm> assemblyTerms = new ArrayList<>();

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

