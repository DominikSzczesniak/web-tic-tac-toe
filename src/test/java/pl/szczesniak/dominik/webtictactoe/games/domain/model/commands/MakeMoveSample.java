package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMoveToMake;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerMoveSample.createAnyPlayerMove;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameIdSample.createAnyTicTacToeGameId;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

public class MakeMoveSample {

	@Builder
	private static MakeMove build(final TicTacToeGameId ticTacToeGameId, final GameMoveToMake playerMove) {
		return new MakeMove(
				ofNullable(ticTacToeGameId).orElse(createAnyTicTacToeGameId()),
				ofNullable(playerMove).orElse(createAnyPlayerMove(createAnyUserId()))
		);
	}

}
