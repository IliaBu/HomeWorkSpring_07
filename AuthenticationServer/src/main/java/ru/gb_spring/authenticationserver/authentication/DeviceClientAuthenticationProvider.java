package ru.gb_spring.authenticationserver.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

/**
 * The type Device client authentication provider.
 */
public final class DeviceClientAuthenticationProvider implements AuthenticationProvider {
	private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-3.2.1";
	private final Log logger = LogFactory.getLog(getClass());
	private final RegisteredClientRepository registeredClientRepository;

    /**
     * Instantiates a new Device client authentication provider.
     *
     * @param registeredClientRepository the registered client repository
     */
    public DeviceClientAuthenticationProvider(RegisteredClientRepository registeredClientRepository) {
		Assert.notNull(registeredClientRepository, "Зарегистрированный Client Repository не может иметь значение null");
		this.registeredClientRepository = registeredClientRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		DeviceClientAuthenticationToken deviceClientAuthentication =
				(DeviceClientAuthenticationToken) authentication;

		if (!ClientAuthenticationMethod.NONE.equals(deviceClientAuthentication.getClientAuthenticationMethod())) {
			return null;
		}

		String clientId = deviceClientAuthentication.getPrincipal().toString();
		RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
		if (registeredClient == null) {
			throwInvalidClient(OAuth2ParameterNames.CLIENT_ID);
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Получен зарегистрированный клиент");
		}

		if (!registeredClient.getClientAuthenticationMethods().contains(
				deviceClientAuthentication.getClientAuthenticationMethod())) {
			throwInvalidClient("Метод аутентификации");
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Проверенные параметры аутентификации клиента устройства");
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Клиент аутентифицированного устройства");
		}

		return new DeviceClientAuthenticationToken(registeredClient,
				deviceClientAuthentication.getClientAuthenticationMethod(), null);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return DeviceClientAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private static void throwInvalidClient(String parameterName) {
		OAuth2Error error = new OAuth2Error(
				OAuth2ErrorCodes.INVALID_CLIENT,
				"Аутентификация клиента устройства не удалась: " + parameterName,
				ERROR_URI
		);
		throw new OAuth2AuthenticationException(error);
	}

}
