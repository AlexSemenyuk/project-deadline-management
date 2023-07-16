package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.TaskConditionCommand;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "task_conditions")
@NoArgsConstructor
@ToString(exclude = "tasks")
@EqualsAndHashCode(exclude = "tasks")
public class TaskCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "taskCondition")
    private List<Task> tasks = new ArrayList<>();

    public TaskCondition(String name) {
        this.name = name;
    }

    public static TaskCondition fromCommand(TaskConditionCommand command) {
        return new TaskCondition(command.name());
    }
}



