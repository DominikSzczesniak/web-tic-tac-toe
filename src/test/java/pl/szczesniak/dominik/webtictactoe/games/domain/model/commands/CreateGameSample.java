package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerName;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerIdSample.createAnyPlayerId;
import static pl.szczesniak.dominik.webtictactoe.games.domain.model.PlayerNameSample.createAnyPlayerName;

public class CreateGameSample {

	@Builder
	private static CreateGame build(final UserId playerOne, final PlayerName playerOneName, final UserId playerTwo, final PlayerName playerTwoName) {
		return new CreateGame(
				ofNullable(playerOne).orElse(createAnyPlayerId()),
				ofNullable(playerOneName).orElse(createAnyPlayerName()),
				ofNullable(playerTwo).orElse(createAnyPlayerId()),
				ofNullable(playerTwoName).orElse(createAnyPlayerName())
		);
	}

}
