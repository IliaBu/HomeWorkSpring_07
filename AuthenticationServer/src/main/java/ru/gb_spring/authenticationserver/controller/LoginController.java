package ru.gb_spring.authenticationserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The type Login controller.
 */
@Controller
public class LoginController {

    /**
     * Login string.
     *
     * @return the string
     */
    @GetMapping("/login")
	public String login() {
		return "login";
	}

}
