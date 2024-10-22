package pl.szczesniak.dominik.webtictactoe.games.domain;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

public class TicTacToeGameSample {
	
	@Builder
	private static TicTacToeGame build(final UserId playerOne, final UserId playerTwo) {
		return new TicTacToeGame(
				ofNullable(playerOne).orElse(createAnyUserId()),
				ofNullable(playerTwo).orElse(createAnyUserId())
		);
	}
	
}
