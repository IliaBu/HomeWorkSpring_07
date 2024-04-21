package ru.gb_spring.authenticationserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The type Device controller.
 */
@Controller
public class DeviceController {

    /**
     * Activate string.
     *
     * @param userCode the user code
     * @return the string
     */
    @GetMapping("/activate")
	public String activate(@RequestParam(value = "user_code", required = false) String userCode) {
		if (userCode != null) {
			return "redirect:/oauth2/device_verification?user_code=" + userCode;
		}
		return "device-activate";
	}

    /**
     * Activated string.
     *
     * @return the string
     */
    @GetMapping("/activated")
	public String activated() {
		return "device-activated";
	}

    /**
     * Success string.
     *
     * @return the string
     */
    @GetMapping(value = "/", params = "success")
	public String success() {
		return "device-activated";
	}

}
