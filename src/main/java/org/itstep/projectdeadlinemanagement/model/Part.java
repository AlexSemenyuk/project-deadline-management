package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.PartCommand;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "parts")
@NoArgsConstructor
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "parts")
    private List<Assembly> assemblies = new ArrayList<>();

    @OneToMany(mappedBy = "part")
    private List<TermPart> termParts = new ArrayList<>();

    @OneToMany(mappedBy = "part")
    private List<ProjectList> projectLists = new ArrayList<>();

    public Part(Integer number, String name) {
        this.number = number;
        this.name = name;
    }

    public static Part fromCommand(PartCommand command) {
        return new Part(command.number(), command.name());
    }
}




