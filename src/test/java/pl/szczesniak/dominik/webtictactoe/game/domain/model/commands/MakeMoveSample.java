package pl.szczesniak.dominik.webtictactoe.game.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;
import pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameId;

import java.util.UUID;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.game.domain.model.PlayerMoveSample.createAnyPlayerMove;
import static pl.szczesniak.dominik.webtictactoe.game.domain.model.TicTacToeGameIdSample.createAnyTicTacToeGameId;

public class MakeMoveSample {

	@Builder
	private static MakeMove build(final TicTacToeGameId ticTacToeGameId, final UUID playerId, final PlayerMove playerMove) {
		return new MakeMove(
				ofNullable(ticTacToeGameId).orElse(createAnyTicTacToeGameId()),
				ofNullable(playerId).orElse(UUID.randomUUID()),
				ofNullable(playerMove).orElse(createAnyPlayerMove())
		);
	}

}
