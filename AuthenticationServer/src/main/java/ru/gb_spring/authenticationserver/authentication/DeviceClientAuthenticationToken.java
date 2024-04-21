package ru.gb_spring.authenticationserver.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;

/**
 * The type Device client authentication token.
 */
@Transient
public class DeviceClientAuthenticationToken extends OAuth2ClientAuthenticationToken {

    /**
     * Instantiates a new Device client authentication token.
     *
     * @param clientId                   the client id
     * @param clientAuthenticationMethod the client authentication method
     * @param credentials                the credentials
     * @param additionalParameters       the additional parameters
     */
    public DeviceClientAuthenticationToken(String clientId, ClientAuthenticationMethod clientAuthenticationMethod,
			@Nullable Object credentials, @Nullable Map<String, Object> additionalParameters) {
		super(clientId, clientAuthenticationMethod, credentials, additionalParameters);
	}

    /**
     * Instantiates a new Device client authentication token.
     *
     * @param registeredClient           the registered client
     * @param clientAuthenticationMethod the client authentication method
     * @param credentials                the credentials
     */
    public DeviceClientAuthenticationToken(RegisteredClient registeredClient, ClientAuthenticationMethod clientAuthenticationMethod,
			@Nullable Object credentials) {
		super(registeredClient, clientAuthenticationMethod, credentials);
	}

}
