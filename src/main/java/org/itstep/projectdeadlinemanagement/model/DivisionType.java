package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.DivisionTypeCommand;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "division_types")
@NoArgsConstructor
@ToString(exclude = "divisions")
@EqualsAndHashCode(exclude = "divisions")
public class DivisionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "divisionType")
    private List<Division> divisions = new ArrayList<>();

    public DivisionType(String name) {
        this.name = name;
    }

    public static DivisionType fromCommand(DivisionTypeCommand command) {
        return new DivisionType(command.name());
    }
}
