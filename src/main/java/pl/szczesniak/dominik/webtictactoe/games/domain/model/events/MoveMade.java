package pl.szczesniak.dominik.webtictactoe.games.domain.model.events;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.DomainEvent;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameState;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@Value
public class MoveMade implements DomainEvent {

	@NonNull TicTacToeGameId gameId;
	@NonNull GameState gameState;

}
