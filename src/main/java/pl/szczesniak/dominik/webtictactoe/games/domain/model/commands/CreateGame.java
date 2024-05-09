package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.NonNull;
import lombok.Value;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@Value
public class CreateGame {

	@NonNull UserId playerOne;
	@NonNull PlayerName playerOneName;
	@NonNull UserId playerTwo;
	@NonNull PlayerName playerTwoName;

}
