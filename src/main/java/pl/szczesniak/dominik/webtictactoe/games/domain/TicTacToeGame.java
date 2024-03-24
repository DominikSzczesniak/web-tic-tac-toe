package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

@EqualsAndHashCode(of = {"gameId"})
@ToString
@Getter
@RequiredArgsConstructor
class TicTacToeGame {

	@NonNull private final TicTacToeGameId gameId;

	@NonNull private final Player playerOne;

	@NonNull private final Player playerTwo;

	private Player nextPlayerToMove;

	void setNextPlayerToMove() {
		if (nextPlayerToMove == null || nextPlayerToMove.equals(playerTwo)) {
			nextPlayerToMove = playerOne;
		} else {
			nextPlayerToMove = playerTwo;
		}
	}

}
