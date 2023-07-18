package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "production_plans")
@NoArgsConstructor
public class ProductionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    Integer number;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Task task;

    @Column(nullable = false)
    private LocalDateTime currentStart;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Equipment equipment;

    public ProductionPlan(Integer number) {
        this.number = number;
    }

    public void setTask(Task task) {
        task.setProductionPlan(this);
        this.task = task;
    }

    public void setEquipment (Equipment equipment) {
        equipment.getProductionPlans().add(this);
        this.equipment = equipment;
    }

}


