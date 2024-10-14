package pl.szczesniak.dominik.webtictactoe.games.domain;

import pl.szczesniak.dominik.webtictactoe.commons.domain.DomainEventsPublisher;
import pl.szczesniak.dominik.webtictactoe.commons.domain.InMemoryEventPublisher;

class TestGamesFacadeConfiguration {

	static GamesFacade gamesFacade(final DomainEventsPublisher publisher, final TicTacToeRulesFactory ticTacToeRulesFactory) {
		return new GamesFacade(new InMemoryTicTacToeGamesRepository(), publisher, ticTacToeRulesFactory);
	}

	static GamesFacade gamesFacade(final TicTacToeRulesFactory ticTacToeRulesFactory, final TicTacToeGamesRepository repository) {
		return new GamesFacade(repository, new InMemoryEventPublisher(), ticTacToeRulesFactory);
	}

}
