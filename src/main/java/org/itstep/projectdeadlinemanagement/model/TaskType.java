package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.command.TaskTypeCommand;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Table(name = "task_types")
@NoArgsConstructor
@ToString(exclude = "tasks")
@EqualsAndHashCode(exclude = "tasks")
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "taskType")
    private List<Task> tasks = new ArrayList<>();

    public TaskType(String name) {
        this.name = name;
    }

    public static TaskType fromCommand(TaskTypeCommand command) {
        return new TaskType(command.name());
    }

}



