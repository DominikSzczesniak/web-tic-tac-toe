package pl.szczesniak.dominik.webtictactoe.games.domain;

class TestGamesFacadeConfiguration {

	static GamesFacade gamesFacade() {
		return new GamesFacade(new GamesService());
	}

}
