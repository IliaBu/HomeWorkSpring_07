package ru.gb_spring.authenticationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import ru.gb_spring.authenticationserver.config.custom.CustomIdentityAuthenticationSuccessHandler;

/**
 * The type Default security config.
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

    /**
     * Default security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers("/css/**", "/img/**", "/login").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(formLogin ->
				formLogin
					.loginPage("/login")
			)
			.oauth2Login(oauth2Login ->
				oauth2Login
					.loginPage("/login")
					.successHandler(authenticationSuccessHandler())
			);

		return http.build();
	}

	private AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomIdentityAuthenticationSuccessHandler();
	}

    /**
     * Users user details service.
     *
     * @return the user details service
     */
    @Bean
	public UserDetailsService users() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();
		UserDetails admin = User.withDefaultPasswordEncoder()
				.username("admin")
				.password("password")
				.roles("USER", "ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user, admin);
	}

    /**
     * Session registry session registry.
     *
     * @return the session registry
     */
    @Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

    /**
     * Http session event publisher http session event publisher.
     *
     * @return the http session event publisher
     */
    @Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
}
