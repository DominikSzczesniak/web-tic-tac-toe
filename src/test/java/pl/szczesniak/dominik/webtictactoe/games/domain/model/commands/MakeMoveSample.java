package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerMoveSample.createAnyPlayerMove;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameIdSample.createAnyTicTacToeGameId;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.UserIdSample.createAnyUserId;

public class MakeMoveSample {

	@Builder
	private static MakeMove build(final TicTacToeGameId ticTacToeGameId, final GameMove playerMove) {
		return new MakeMove(
				ofNullable(ticTacToeGameId).orElse(createAnyTicTacToeGameId()),
				ofNullable(playerMove).orElse(createAnyPlayerMove(createAnyUserId()))
		);
	}

}
