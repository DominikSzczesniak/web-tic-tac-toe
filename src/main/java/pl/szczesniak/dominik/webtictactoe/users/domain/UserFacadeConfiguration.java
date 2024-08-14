package pl.szczesniak.dominik.webtictactoe.users.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserFacadeConfiguration {

	@Bean
	public UserFacade userFacade(final UserRepository userRepository) {
		return new UserFacade(userRepository);
	}

}
