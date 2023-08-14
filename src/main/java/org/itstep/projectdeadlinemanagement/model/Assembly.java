package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.AssemblyCommand;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "assemblies")
@NoArgsConstructor
public class  Assembly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "assembly")
    private List<AssemblyList> assemblyLists = new ArrayList<>();

    @ManyToMany(mappedBy = "assemblies")
    private List<AssemblyList> assemblyListsEntry = new ArrayList<>();

    @ManyToMany(mappedBy = "assemblies")
    private List<PartList> partLists = new ArrayList<>();

    @OneToMany(mappedBy = "assembly")
    private List<TechnologyAssembly> TechnologyAssemblies = new ArrayList<>();

    public Assembly(Integer number, String name) {
        this.number = number;
        this.name = name;
    }

    public static Assembly fromCommand(AssemblyCommand command) {
        return new Assembly (command.number(), command.name());
    }
}



