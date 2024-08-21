package pl.szczesniak.dominik.webtictactoe.users.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class TestUserFacadeConfiguration {

	static UserFacade userFacade() {
		return new UserFacade(new InMemoryUserRepository(), new BCryptPasswordEncoder());
	}

}
