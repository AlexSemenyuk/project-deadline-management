package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.TermPartCommand;



@Entity
@Data
@Table(name = "term_parts")
@NoArgsConstructor
@ToString(exclude = {"part", "equipment"})
@EqualsAndHashCode(exclude = {"part", "equipment"})
public class TermPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer operationTime;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Part part;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Equipment equipment;


    public TermPart(Integer number, Integer operationTime) {
        this.number = number;
        this.operationTime = operationTime;
    }

    public void setPart (Part part) {
        part.getPartTerms().add(this);
        this.part = part;
    }

    public void setEquipment (Equipment equipment) {
        equipment.getPartTerms().add(this);
        this.equipment = equipment;
    }

    public static TermPart fromCommand(TermPartCommand command) {
        return new TermPart(command.number(), command.operationTime());
    }
}


