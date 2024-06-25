package pl.szczesniak.dominik.webtictactoe.games.domain;

import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;

class TestGamesFacadeConfiguration {

	static GamesFacade gamesFacade(final DomainEventsPublisher publisher) {
		return new GamesFacade(new GamesService(new InMemoryTicTacToeGamesRepository(), publisher));
	}

}
