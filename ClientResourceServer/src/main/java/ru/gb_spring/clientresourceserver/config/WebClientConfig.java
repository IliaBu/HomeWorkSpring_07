package ru.gb_spring.clientresourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.gb_spring.clientresourceserver.authorization.DeviceCodeOAuth2AuthorizedClientProvider;

/**
 * The type Web client config.
 */
@Configuration
public class WebClientConfig {

    /**
     * Web client web client.
     *
     * @param authorizedClientManager the authorized client manager
     * @return the web client
     */
    @Bean
	public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
				new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		return WebClient.builder()
				.apply(oauth2Client.oauth2Configuration())
				.build();
	}

    /**
     * Authorized client manager o auth 2 authorized client manager.
     *
     * @param clientRegistrationRepository the client registration repository
     * @param authorizedClientRepository   the authorized client repository
     * @return the o auth 2 authorized client manager
     */
    @Bean
	public OAuth2AuthorizedClientManager authorizedClientManager(
			ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository) {

		OAuth2AuthorizedClientProvider authorizedClientProvider =
				OAuth2AuthorizedClientProviderBuilder.builder()
						.authorizationCode()
						.refreshToken()
						.clientCredentials()
						.provider(new DeviceCodeOAuth2AuthorizedClientProvider())
						.build();

		DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientRepository);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		authorizedClientManager.setContextAttributesMapper(DeviceCodeOAuth2AuthorizedClientProvider
				.deviceCodeContextAttributesMapper());

		return authorizedClientManager;
	}

}
