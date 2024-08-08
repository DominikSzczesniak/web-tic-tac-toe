package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.MyPlayerMove;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameId;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerIdSample.createAnyPlayerId;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerMoveSample.createAnyPlayerMove;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.TicTacToeGameIdSample.createAnyTicTacToeGameId;

public class MakeMoveSample {

	@Builder
	private static MakeMove build(final TicTacToeGameId ticTacToeGameId, final UserId playerId, final MyPlayerMove playerMove) {
		return new MakeMove(
				ofNullable(ticTacToeGameId).orElse(createAnyTicTacToeGameId()),
				ofNullable(playerId).orElse(createAnyPlayerId()),
				ofNullable(playerMove).orElse(createAnyPlayerMove(playerId))
		);
	}

}
