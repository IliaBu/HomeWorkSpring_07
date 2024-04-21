package ru.gb_spring.clientresourceserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The type Default controller.
 */
@Controller
public class DefaultController {

    /**
     * Root string.
     *
     * @return the string
     */
    @GetMapping("/")
	public String root() {
		return "redirect:/index";
	}

    /**
     * Index string.
     *
     * @return the string
     */
    @GetMapping("/index")
	public String index() {
		return "redirect:/users";
	}

    /**
     * Logged out string.
     *
     * @return the string
     */
    @GetMapping("/logged-out")
	public String loggedOut() {
		return "logged-out";
	}

}
