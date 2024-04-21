package ru.gb_spring.clientresourceserver.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;

import java.time.Clock;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * The type Device code o auth 2 authorized client provider.
 */
public final class DeviceCodeOAuth2AuthorizedClientProvider implements OAuth2AuthorizedClientProvider {

	private OAuth2AccessTokenResponseClient<OAuth2DeviceGrantRequest> accessTokenResponseClient =
			new OAuth2DeviceAccessTokenResponseClient();

	private Duration clockSkew = Duration.ofSeconds(60);

	private Clock clock = Clock.systemUTC();

    /**
     * Sets access token response client.
     *
     * @param accessTokenResponseClient the access token response client
     */
    public void setAccessTokenResponseClient(OAuth2AccessTokenResponseClient<OAuth2DeviceGrantRequest> accessTokenResponseClient) {
		this.accessTokenResponseClient = accessTokenResponseClient;
	}

    /**
     * Sets clock skew.
     *
     * @param clockSkew the clock skew
     */
    public void setClockSkew(Duration clockSkew) {
		this.clockSkew = clockSkew;
	}

    /**
     * Sets clock.
     *
     * @param clock the clock
     */
    public void setClock(Clock clock) {
		this.clock = clock;
	}

	@Override
	public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {
		Assert.notNull(context, "контекст не может быть нулевым");
		ClientRegistration clientRegistration = context.getClientRegistration();
		if (!AuthorizationGrantType.DEVICE_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
			return null;
		}
		OAuth2AuthorizedClient authorizedClient = context.getAuthorizedClient();
		if (authorizedClient != null && !hasTokenExpired(authorizedClient.getAccessToken())) {
			return null;
		}
		if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
			return null;
		}
		String deviceCode = context.getAttribute(OAuth2ParameterNames.DEVICE_CODE);
		OAuth2DeviceGrantRequest deviceGrantRequest = new OAuth2DeviceGrantRequest(clientRegistration, deviceCode);
		OAuth2AccessTokenResponse tokenResponse = getTokenResponse(clientRegistration, deviceGrantRequest);
		return new OAuth2AuthorizedClient(clientRegistration, context.getPrincipal().getName(),
				tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
	}

	private OAuth2AccessTokenResponse getTokenResponse(ClientRegistration clientRegistration,
			OAuth2DeviceGrantRequest deviceGrantRequest) {
		try {
			return this.accessTokenResponseClient.getTokenResponse(deviceGrantRequest);
		} catch (OAuth2AuthorizationException ex) {
			throw new ClientAuthorizationException(ex.getError(), clientRegistration.getRegistrationId(), ex);
		}
	}

	private boolean hasTokenExpired(OAuth2Token token) {
		return this.clock.instant().isAfter(token.getExpiresAt().minus(this.clockSkew));
	}

    /**
     * Device code context attributes mapper function.
     *
     * @return the function
     */
    public static Function<OAuth2AuthorizeRequest, Map<String, Object>> deviceCodeContextAttributesMapper() {
		return (authorizeRequest) -> {
			HttpServletRequest request = authorizeRequest.getAttribute(HttpServletRequest.class.getName());
			Assert.notNull(request, "запрос не может быть нулевым");

			String deviceCode = request.getParameter(OAuth2ParameterNames.DEVICE_CODE);
			return (deviceCode != null) ? Collections.singletonMap(OAuth2ParameterNames.DEVICE_CODE, deviceCode) :
					Collections.emptyMap();
		};
	}

}
