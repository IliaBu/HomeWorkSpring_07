package ru.gb_spring.clientresourceserver.authorization;

import lombok.Getter;
import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

/**
 * The type O auth 2 device grant request.
 */
@Getter
public final class OAuth2DeviceGrantRequest extends AbstractOAuth2AuthorizationGrantRequest {

	private final String deviceCode;

    /**
     * Instantiates a new O auth 2 device grant request.
     *
     * @param clientRegistration the client registration
     * @param deviceCode         the device code
     */
    public OAuth2DeviceGrantRequest(ClientRegistration clientRegistration, String deviceCode) {
		super(AuthorizationGrantType.DEVICE_CODE, clientRegistration);
		Assert.hasText(deviceCode, "код устройства не может быть пустым");
		this.deviceCode = deviceCode;
	}

}
