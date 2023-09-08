package org.itstep.projectdeadlinemanagement.controller;


import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.UserDetailManagerCommand;
import org.itstep.projectdeadlinemanagement.model.UserRole;
import org.itstep.projectdeadlinemanagement.repository.UserRoleRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final PasswordEncoder encoder;
    private final UserDetailsManager userDetailsManager;
    private final UserRoleRepository userRoleRepository;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userRoles", userRoleRepository.findAll());
        return "registration";
    }


    @PostMapping("/registration")
    public String createUser(UserDetailManagerCommand command) {
        List<UserRole> userRoles = userRoleRepository.findAllById(command.userRolesIds());
        List<String> userRoleNames = new ArrayList<>();
        userRoles.forEach(userRole -> userRoleNames.add(userRole.getName()));
//        command.userRolesIds().forEach(System.out::println);
//        userRoles.forEach(System.out::println);
//        System.out.println("userRoleNames = " + String.valueOf(userRoleNames));
//        System.out.println("command.userName() = " + command.userName());
//        System.out.println("command.password() = " + command.password());
//        System.out.println("command.passwordConfirm() = " + command.passwordConfirm());
        if (command.isPasswordsEquals()){
            UserDetails user = User.builder()
                    .username(command.userName())
                    .roles(String.valueOf(userRoleNames))
                    .password(encoder.encode(command.password()))
                    .build();
            userDetailsManager.createUser(user);
        }
        return "/registration";
    }


//    @ResponseBody
//    @GetMapping("/encode")
//    public String encode(String password) {
//        return encoder.encode(password);
//    }
}



