package pl.szczesniak.dominik.webtictactoe.game.domain.model.commands;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

@Value
public class MakeMove {

	@NonNull TicTacToeGameId gameId;
	@NonNull PlayerName playerName;
	@NonNull PlayerMove playerMove;

}
