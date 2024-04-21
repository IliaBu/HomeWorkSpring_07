package ru.gb_spring.clientresourceserver.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.*;


/**
 * The type Device controller.
 */
@Controller
public class DeviceController {

	private static final Set<String> DEVICE_GRANT_ERRORS = new HashSet<>(Arrays.asList(
			"authorization_pending",
			"slow_down",
			"access_denied",
			"expired_token"
	));

	private static final ParameterizedTypeReference<Map<String, Object>> TYPE_REFERENCE =
			new ParameterizedTypeReference<>() {};

	private final ClientRegistrationRepository clientRegistrationRepository;

	private final WebClient webClient;

    /**
     * Instantiates a new Device controller.
     *
     * @param clientRegistrationRepository the client registration repository
     * @param webClient                    the web client
     */
    public DeviceController(ClientRegistrationRepository clientRegistrationRepository, WebClient webClient) {

		this.clientRegistrationRepository = clientRegistrationRepository;
		this.webClient = webClient;
	}

    /**
     * Authorize string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/device_authorize")
	public String authorize(Model model) {
		ClientRegistration clientRegistration =
				this.clientRegistrationRepository.findByRegistrationId(
						"reg-client-device-code");

		MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
		requestParameters.add(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
		requestParameters.add(OAuth2ParameterNames.SCOPE, StringUtils.collectionToDelimitedString(
				clientRegistration.getScopes(), " "));

		String deviceAuthorizationUri = (String) clientRegistration.getProviderDetails().getConfigurationMetadata().get("device_authorization_endpoint");

		Map<String, Object> responseParameters =
				this.webClient.post()
						.uri(deviceAuthorizationUri)
						.headers(headers -> {
							if (!clientRegistration.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
								headers.setBasicAuth(clientRegistration.getClientId(), clientRegistration.getClientSecret());
							}
						})
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.body(BodyInserters.fromFormData(requestParameters))
						.retrieve()
						.bodyToMono(TYPE_REFERENCE)
						.block();

		Objects.requireNonNull(responseParameters, "Ответ на авторизацию устройства не может быть нулевым");
		Instant issuedAt = Instant.now();
		Integer expiresIn = (Integer) responseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
		Instant expiresAt = issuedAt.plusSeconds(expiresIn);

		model.addAttribute("deviceCode", responseParameters.get(OAuth2ParameterNames.DEVICE_CODE));
		model.addAttribute("expiresAt", expiresAt);
		model.addAttribute("userCode", responseParameters.get(OAuth2ParameterNames.USER_CODE));
		model.addAttribute("verificationUri", responseParameters.get(OAuth2ParameterNames.VERIFICATION_URI));
		model.addAttribute("verificationUriComplete", responseParameters.get(
				OAuth2ParameterNames.VERIFICATION_URI_COMPLETE));

		return "device-authorize";
	}

    /**
     * Poll response entity.
     *
     * @param deviceCode       the device code
     * @param authorizedClient the authorized client
     * @return the response entity
     */
    @PostMapping("/device_authorize")
	public ResponseEntity<Void> poll(@RequestParam(OAuth2ParameterNames.DEVICE_CODE) String deviceCode,
									 @RegisteredOAuth2AuthorizedClient("reg-client-device-code")
									 OAuth2AuthorizedClient authorizedClient) {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

    /**
     * Handle error response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ExceptionHandler(OAuth2AuthorizationException.class)
	public ResponseEntity<OAuth2Error> handleError(OAuth2AuthorizationException ex) {
		String errorCode = ex.getError().getErrorCode();
		if (DEVICE_GRANT_ERRORS.contains(errorCode))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getError());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getError());
	}

    /**
     * Authorized string.
     *
     * @param authorizedClient the authorized client
     * @return the string
     */
    @GetMapping("/device_authorized")
	public String authorized(@RegisteredOAuth2AuthorizedClient("reg-client-device-code")
							 OAuth2AuthorizedClient authorizedClient) {
		return "redirect:/index";
	}

}