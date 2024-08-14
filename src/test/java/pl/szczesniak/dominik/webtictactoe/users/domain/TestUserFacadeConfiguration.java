package pl.szczesniak.dominik.webtictactoe.users.domain;

class TestUserFacadeConfiguration {

	static UserFacade userFacade() {
		return new UserFacade(new InMemoryUserRepository());
	}

}
