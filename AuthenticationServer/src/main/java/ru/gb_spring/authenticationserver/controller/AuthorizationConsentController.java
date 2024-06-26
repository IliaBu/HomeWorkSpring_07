package ru.gb_spring.authenticationserver.controller;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

/**
 * The type Authorization consent controller.
 */
@Controller
public class AuthorizationConsentController {
	private final RegisteredClientRepository registeredClientRepository;
	private final OAuth2AuthorizationConsentService authorizationConsentService;

    /**
     * Instantiates a new Authorization consent controller.
     *
     * @param registeredClientRepository  the registered client repository
     * @param authorizationConsentService the authorization consent service
     */
    public AuthorizationConsentController(RegisteredClientRepository registeredClientRepository,
			OAuth2AuthorizationConsentService authorizationConsentService) {
		this.registeredClientRepository = registeredClientRepository;
		this.authorizationConsentService = authorizationConsentService;
	}

    /**
     * Consent string.
     *
     * @param principal the principal
     * @param model     the model
     * @param clientId  the client id
     * @param scope     the scope
     * @param state     the state
     * @param userCode  the user code
     * @return the string
     */
    @GetMapping(value = "/oauth2/consent")
	public String consent(Principal principal, Model model,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			@RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

		Set<String> scopesToApprove = new HashSet<>();
		Set<String> previouslyApprovedScopes = new HashSet<>();
		RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
		OAuth2AuthorizationConsent currentAuthorizationConsent =
				this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
		Set<String> authorizedScopes;
		if (currentAuthorizationConsent != null) {
			authorizedScopes = currentAuthorizationConsent.getScopes();
		} else {
			authorizedScopes = Collections.emptySet();
		}
		for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
			if (OidcScopes.OPENID.equals(requestedScope)) {
				continue;
			}
			if (authorizedScopes.contains(requestedScope)) {
				previouslyApprovedScopes.add(requestedScope);
			} else {
				scopesToApprove.add(requestedScope);
			}
		}

		model.addAttribute("clientId", clientId);
		model.addAttribute("state", state);
		model.addAttribute("scopes", withDescription(scopesToApprove));
		model.addAttribute("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
		model.addAttribute("principalName", principal.getName());
		model.addAttribute("userCode", userCode);
		if (StringUtils.hasText(userCode)) {
			model.addAttribute("requestURI", "/oauth2/device_verification");
		} else {
			model.addAttribute("requestURI", "/oauth2/authorize");
		}

		return "consent";
	}

	private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
		Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
		for (String scope : scopes) {
			scopeWithDescriptions.add(new ScopeWithDescription(scope));

		}
		return scopeWithDescriptions;
	}

    /**
     * The type Scope with description.
     */
    public static class ScopeWithDescription {
		private static final String DEFAULT_DESCRIPTION = "НЕИЗВЕСТНАЯ ОБЛАСТЬ - Мы не можем предоставить информацию об этом разрешении, будьте осторожны при его предоставлении.";
		private static final Map<String, String> scopeDescriptions = new HashMap<>();
		static {
			scopeDescriptions.put(
					OidcScopes.PROFILE,
					"Это приложение сможет читать информацию вашего профиля."
			);
			scopeDescriptions.put(
					"user.read",
					"Это приложение сможет прочитать ваше сообщение."
			);
			scopeDescriptions.put(
					"user.write",
					"Это приложение сможет добавлять новые сообщения. Он также сможет редактировать и удалять существующие сообщения."
			);
			scopeDescriptions.put(
					"other.scope",
					"Другая область действия."
			);
		}

        /**
         * The Scope.
         */
        public final String scope;
        /**
         * The Description.
         */
        public final String description;

        /**
         * Instantiates a new Scope with description.
         *
         * @param scope the scope
         */
        ScopeWithDescription(String scope) {
			this.scope = scope;
			this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
		}
	}

}
