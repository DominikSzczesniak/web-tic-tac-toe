package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class MakeMove {

	@NonNull
	TicTacToeGameId gameId;
	@NonNull
	UserId playerId;
	@NonNull
	MyPlayerMove playerMove;

}
