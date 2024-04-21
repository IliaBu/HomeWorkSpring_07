package ru.gb_spring.authenticationserver.config.custom;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The type User repository o auth 2 user handler.
 */
public final class UserRepositoryOAuth2UserHandler implements Consumer<OAuth2User> {
	private final UserRepository userRepository = new UserRepository();

	@Override
	public void accept(OAuth2User user) {
		if (this.userRepository.findByName(user.getName()) == null) this.userRepository.save(user);
	}

    /**
     * The type User repository.
     */
    static class UserRepository {
		private final Map<String, OAuth2User> userCache = new ConcurrentHashMap<>();

        /**
         * Find by name o auth 2 user.
         *
         * @param name the name
         * @return the o auth 2 user
         */
        public OAuth2User findByName(String name) {
			return this.userCache.get(name);
		}

        /**
         * Save.
         *
         * @param oauth2User the oauth 2 user
         */
        public void save(OAuth2User oauth2User) {
			this.userCache.put(oauth2User.getName(), oauth2User);
		}

	}

}
