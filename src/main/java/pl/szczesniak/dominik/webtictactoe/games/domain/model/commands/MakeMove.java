package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@Value
public class MakeMove {

	@NonNull TicTacToeGameId gameId;
	@NonNull MyPlayerMove playerMove;

}
