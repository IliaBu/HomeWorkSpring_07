package ru.gb_spring.clientresourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * The type Security config.
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    /**
     * Web security customizer web security customizer.
     *
     * @return the web security customizer
     */
    @Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/webjars/**", "/css/**", "/img/**");
	}

    /**
     * Security filter chain security filter chain.
     *
     * @param http                         the http
     * @param clientRegistrationRepository the client registration repository
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			ClientRegistrationRepository clientRegistrationRepository) throws Exception {
		http
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers("/logged-out").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2Login(oauth2Login ->
				oauth2Login.loginPage("/oauth2/authorization/reg-client-oidc"))
			.oauth2Client(withDefaults())
			.logout(logout ->
				logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)));
		return http.build();
	}

	private LogoutSuccessHandler oidcLogoutSuccessHandler(
			ClientRegistrationRepository clientRegistrationRepository) {
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
				new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/logged-out");

		return oidcLogoutSuccessHandler;
	}

}
