package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "divisions")
@NoArgsConstructor
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private DivisionType divisionType;

    @OneToMany(mappedBy = "division")
    private List<Equipment> equipments = new ArrayList<>();

    public Division(String name) {
        this.name = name;
    }

    public void setDivisionType (DivisionType divisionType) {
        divisionType.getDivisions().add(this);
        this.divisionType = divisionType;
    }

    public static Division fromCommand(DivisionCommand command) {
        return new Division (command.name());
    }
}
