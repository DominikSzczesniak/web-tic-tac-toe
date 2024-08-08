package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.exceptions.OtherPlayerTurnException;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY;

@ToString
@Getter
@EqualsAndHashCode(of = {"id"})
class TicTacToeGame {

	@Id
	private Long gameId;

	private final String id;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "player_one_id_")
	private final UserId playerOne;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "player_two_id_")
	private final UserId playerTwo;

	@MappedCollection(idColumn = "game_id", keyColumn = "move_key")
	private final List<MyPlayerMove> moves;

	public TicTacToeGame(@NonNull final UserId playerOne, @NonNull final UserId playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		this.id = UUID.randomUUID().toString();
		this.moves = new ArrayList<>();
	}

	@PersistenceCreator
	TicTacToeGame(@NonNull final UserId playerTwo, @NonNull final UserId playerOne, final Long gameId, final String id, List<MyPlayerMove> moves) {
		this.playerTwo = playerTwo;
		this.playerOne = playerOne;
		this.gameId = gameId;
		this.id = id;
		this.moves = moves != null ? new ArrayList<>(moves) : new ArrayList<>();
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


