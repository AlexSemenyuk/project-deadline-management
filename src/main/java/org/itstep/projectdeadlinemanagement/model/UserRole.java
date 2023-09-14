package org.itstep.projectdeadlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.UserRoleCommand;

@Entity
@Data
@Table(name = "user_roles")
@NoArgsConstructor
//@ToString(exclude = "equipments")
//@EqualsAndHashCode(exclude = "equipments")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    public UserRole(String name) {
        this.name = name;
    }

    public static UserRole fromCommand(UserRoleCommand command) {
        return new UserRole(command.name());
    }
}


