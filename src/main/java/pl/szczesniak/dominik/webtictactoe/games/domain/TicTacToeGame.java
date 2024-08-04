package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.ArrayList;
import java.util.List;

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

	@Embedded(onEmpty = USE_EMPTY, prefix = "player_to_move")
	private UserId nextPlayerToMove;

	private final List<MyPlayerMove> moves = new ArrayList<>();

	public TicTacToeGame(@NonNull final UserId playerOne, @NonNull final UserId playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}

	void setNextPlayerToMove() {
		if (nextPlayerToMove == null || nextPlayerToMove.equals(playerTwo)) {
			nextPlayerToMove = playerOne;
		} else {
			nextPlayerToMove = playerTwo;
		}
	}

	TicTacToeGameId getGameId() {
		return new TicTacToeGameId(gameId);
	}

	void addMove(final MyPlayerMove move) {
		moves.add(move);
	}

	void setPlayerToMoveToDefault() {
		nextPlayerToMove = playerOne;
	}

}


