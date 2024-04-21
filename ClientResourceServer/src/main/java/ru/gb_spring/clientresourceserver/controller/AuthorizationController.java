package ru.gb_spring.clientresourceserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;


/**
 * The type Authorization controller.
 */
@Controller
public class AuthorizationController {

    /**
     * Authorization failed string.
     *
     * @param model   the model
     * @param request the request
     * @return the string
     */
    @GetMapping(value = "/authorized", params = OAuth2ParameterNames.ERROR)
	public String authorizationFailed(Model model, HttpServletRequest request) {
		String errorCode = request.getParameter(OAuth2ParameterNames.ERROR);
		if (StringUtils.hasText(errorCode)) {
			model.addAttribute("error",
					new OAuth2Error(
							errorCode,
							request.getParameter(OAuth2ParameterNames.ERROR_DESCRIPTION),
							request.getParameter(OAuth2ParameterNames.ERROR_URI))
			);
		}

		return "index";
	}

    /**
     * Device code grant string.
     *
     * @return the string
     */
    @GetMapping(value = "/authorize", params = "grant_type=device_code")
	public String deviceCodeGrant() {
		return "device-activate";
	}

    /**
     * Handle error string.
     *
     * @param model the model
     * @param ex    the ex
     * @return the string
     */
    @ExceptionHandler(WebClientResponseException.class)
	public String handleError(Model model, WebClientResponseException ex) {
		model.addAttribute("error", ex.getMessage());
		return "index";
	}

}