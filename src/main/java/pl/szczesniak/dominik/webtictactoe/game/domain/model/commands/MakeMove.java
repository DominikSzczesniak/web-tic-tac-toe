package pl.szczesniak.dominik.webtictactoe.game.domain.model.commands;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

import java.util.UUID;

@Value
public class MakeMove {

	@NonNull TicTacToeGameId gameId;
	@NonNull UUID playerId;
	@NonNull PlayerMove playerMove;

}
