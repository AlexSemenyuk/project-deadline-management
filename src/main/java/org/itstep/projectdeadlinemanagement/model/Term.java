package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.TermCommand;



@Entity
@Data
@Table(name = "terms")
@NoArgsConstructor
@ToString(exclude = {"part", "equipment"})
@EqualsAndHashCode(exclude = {"part", "equipment"})
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer number;

//    @Column(nullable = false)
//    private String name;

    @Column(nullable = false)
    private Integer operationTime;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Part part;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Equipment equipment;


    public Term (Integer number, Integer operationTime) {
        this.number = number;
        this.operationTime = operationTime;
    }

    public void setPart (Part part) {
        part.getTerms().add(this);
        this.part = part;
    }

    public void setEquipment (Equipment equipment) {
        equipment.getTerms().add(this);
        this.equipment = equipment;
    }

    public static Term fromCommand(TermCommand command) {
        return new Term(command.number(), command.operationTime());
    }
}


