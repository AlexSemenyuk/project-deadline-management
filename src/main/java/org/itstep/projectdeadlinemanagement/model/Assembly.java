package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;
import org.itstep.projectdeadlinemanagement.command.DivisionCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "assemblies")
@NoArgsConstructor
public class Assembly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private Set<Author> authors = new HashSet<>();
    private List<Part> parts = new ArrayList<>();

    public Assembly(Integer number, String name) {
        this.number = number;
        this.name = name;
    }

    public void addPart(Part part) {
        part.getAssemblies().add(this);
        parts.add(part);
    }

    public static Assembly fromCommand(AssemblyCommand command) {
        return new Assembly (command.number(), command.name());
    }
}



