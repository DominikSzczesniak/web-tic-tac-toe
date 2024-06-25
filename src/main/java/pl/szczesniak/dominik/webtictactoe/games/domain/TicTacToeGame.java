package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static org.springframework.data.relational.core.mapping.Embedded.OnEmpty.USE_EMPTY;

@ToString
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"gameId"})
class TicTacToeGame {

	@NonNull
	@Id
	@Embedded(onEmpty = USE_EMPTY, prefix = "game_id")
	private final TicTacToeGameId gameId;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "player_one")
	private final UserId playerOne;

	@NonNull
	@Embedded(onEmpty = USE_EMPTY, prefix = "player_two")
	private final UserId playerTwo;

	@Embedded(onEmpty = USE_EMPTY, prefix = "player_to_move")
	private UserId nextPlayerToMove;

	void setNextPlayerToMove() {
		if (nextPlayerToMove == null || nextPlayerToMove.equals(playerTwo)) {
			nextPlayerToMove = playerOne;
		} else {
			nextPlayerToMove = playerTwo;
		}
	}

}


