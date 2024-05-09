package pl.szczesniak.dominik.webtictactoe.games.infrastructure.incoming.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.szczesniak.dominik.webtictactoe.games.domain.GamesFacade;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.commands.CreateGame;
import pl.szczesniak.dominik.webtictactoe.matchmaking.domain.model.events.PlayersMatched;

@Component
@RequiredArgsConstructor
class PlayersMatchedEventListener {

	private final GamesFacade gamesFacade;

	@EventListener(PlayersMatched.class)
	public void handlePlayersMatchedEvent(final PlayersMatched event) {
		gamesFacade.createGame(new CreateGame(event.getPlayerOne(), event.getPlayerOneName(), event.getPlayerTwo(), event.getPlayerTwoName()));
	}

}
