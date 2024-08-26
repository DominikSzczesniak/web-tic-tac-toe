package pl.szczesniak.dominik.webtictactoe.games;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.events.MoveMade;

@Component
@RequiredArgsConstructor
class MoveMadeInvoker {

	private final ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	void moveMade(final Long gameId, final GameState gameState) {
		applicationEventPublisher.publishEvent(new MoveMade(new TicTacToeGameId(gameId), gameState));
	}

}
