package pl.szczesniak.dominik.webtictactoe.game.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.Player;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

@EqualsAndHashCode
@ToString
@Getter
public class TicTacToeGame {

	private final Player playerOne;

	private final Player playerTwo;

	private final TicTacToeGameId gameId;

	private Player nextPlayerToMove;

	TicTacToeGame(@NonNull final Player playerOne, @NonNull final Player playerTwo, @NonNull final TicTacToeGameId gameId) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		this.gameId = gameId;
	}

	void setNextPlayerToMove() {
		if (nextPlayerToMove == null || nextPlayerToMove.equals(playerTwo)) {
			this.nextPlayerToMove = playerOne;
		} else {
			this.nextPlayerToMove = playerTwo;
		}
	}

	Player getPlayerByName(final PlayerName playerName) {
		if (playerOne.getName().equals(playerName)) {
			return playerOne;
		}
		return playerTwo;
	}

}
