package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

@ToString
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"gameId"})
class TicTacToeGame {

	@NonNull private final TicTacToeGameId gameId;

	@NonNull private final UserId playerOne;

	@NonNull private final UserId playerTwo;

	private UserId nextPlayerToMove;

	void setNextPlayerToMove() {
		if (nextPlayerToMove == null || nextPlayerToMove.equals(playerTwo)) {
			nextPlayerToMove = playerOne;
		} else {
			nextPlayerToMove = playerTwo;
		}
	}

}


