package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@Value
public class MakeMove {

	@NonNull TicTacToeGameId gameId;
	@NonNull GameMove playerMove;

}
