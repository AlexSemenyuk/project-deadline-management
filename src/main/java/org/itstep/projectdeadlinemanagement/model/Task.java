package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "tasks")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "productionPlan")
@ToString(exclude = "productionPlan")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer projectNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Equipment equipment;

    @Column(nullable = false)
    private Integer partNumber;

    @Column(nullable = false)
    private String partName;

    @Column(nullable = false)
    private Integer termNumber;

    @Column(nullable = false)
    private Integer operationTime;

    @Column(nullable = false)
    private Integer lotNumber;

    @Column(nullable = false)
    private LocalDateTime startProduction;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private TaskCondition taskCondition;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Project project;

    @OneToOne(mappedBy = "task")
    private ProductionPlan productionPlan;

    public Task(Integer projectNumber, Integer partNumber, String partName, Integer termNumber,
                Integer operationTime, Integer lotNumber, LocalDateTime startProduction) {
        this.projectNumber = projectNumber;
        this.partNumber = partNumber;
        this.partName = partName;
        this.termNumber = termNumber;
        this.operationTime = operationTime;
        this.lotNumber = lotNumber;
        this.startProduction = startProduction;
    }

    public void setEquipment(Equipment equipment) {
        equipment.getTasks().add(this);
        this.equipment = equipment;
    }

    public void setTaskCondition(TaskCondition taskCondition) {
        taskCondition.getTasks().add(this);
        this.taskCondition = taskCondition;
    }
    public void setProject(Project project) {
        project.getTasks().add(this);
        this.project = project;
    }

    public static Task formTask(Integer projectNumber, Integer partNumber, String partName,
                                Integer termNumber, Integer operationTime, Integer lotNumber, LocalDateTime startProduction) {
        return new Task(projectNumber, partNumber, partName, termNumber, operationTime, lotNumber, startProduction);
    }

}




