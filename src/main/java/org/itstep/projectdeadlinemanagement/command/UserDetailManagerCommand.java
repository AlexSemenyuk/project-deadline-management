package org.itstep.projectdeadlinemanagement.command;

import java.util.List;
import java.util.Objects;

public record UserDetailManagerCommand(
        String userName,
        String password,
        String passwordConfirm,
        List<Integer> userRolesIds
) {
    public boolean isPasswordsEquals() {
        return Objects.equals(password, passwordConfirm);
    }
}
