package pl.szczesniak.dominik.webtictactoe.users.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class UserFacadeConfiguration {

	@Bean
	public UserFacade userFacade(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
		return new UserFacade(userRepository, passwordEncoder);
	}

}
