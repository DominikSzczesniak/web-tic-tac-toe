package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY;

@ToString
@Getter
@EqualsAndHashCode(of = {"gameId"})
class TicTacToeGame {

	@Id
	private Long gameId;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "player_one")
	private final UserId playerOne;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "player_two")
	private final UserId playerTwo;

	private final List<MyPlayerMove> moves = new ArrayList<>();

	public TicTacToeGame(@NonNull final UserId playerOne, @NonNull final UserId playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}

	TicTacToeGameId getGameId() {
		return new TicTacToeGameId(gameId);
	}

	void addMove(final MyPlayerMove currentMove) {
		checkIsPlayerMove(currentMove);
		moves.add(currentMove);
	}

	private void checkIsPlayerMove(final MyPlayerMove currentMove) {
		getLastMove().ifPresentOrElse(
				lastMove -> checkOtherPlayerThanLastTime(currentMove, lastMove),
				() -> checkFirstPlayer(currentMove)
		);
	}

	private void checkFirstPlayer(final MyPlayerMove currentMove) {
		if (currentMove.getPlayer().equals(playerTwo)) {
			throw new OtherPlayerTurnException("The first move must be made by Player One.");
		}
	}

	private static void checkOtherPlayerThanLastTime(final MyPlayerMove currentMove, final MyPlayerMove lastMove) {
		if (lastMove.getPlayer().equals(currentMove.getPlayer())) {
			throw new OtherPlayerTurnException("Same player tried to make a move.");
		}
	}

	private Optional<MyPlayerMove> getLastMove() {
		return moves.isEmpty() ? Optional.empty() : Optional.of(moves.get(moves.size() - 1));
	}

	UserId getNextPlayerToMove() {
		final Optional<UserId> lastPlayer = getLastMove().map(MyPlayerMove::getPlayer);
		if (lastPlayer.isEmpty()) {
			return playerOne;
		}
		return lastPlayer.get().equals(playerOne) ? playerTwo : playerOne;
	}

}


