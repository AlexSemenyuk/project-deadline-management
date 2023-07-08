package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final PasswordEncoder encoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @ResponseBody
    @GetMapping("/encode")
    public String encode(String password) {
        return encoder.encode(password);
    }
}

