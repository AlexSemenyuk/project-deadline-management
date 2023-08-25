package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.EquipmentTypeCommand;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "equipment_types")
@NoArgsConstructor
@ToString(exclude = "equipments")
@EqualsAndHashCode(exclude = "equipments")
public class EquipmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "equipmentType")
    private List<Equipment> equipments = new ArrayList<>();

    public EquipmentType(String name) {
        this.name = name;
    }

    public static EquipmentType fromCommand(EquipmentTypeCommand command) {
        return new EquipmentType(command.name());
    }
}
