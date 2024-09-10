package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

public class CreateGameSample {

	@Builder
	private static CreateGame build(final UserId playerOne, final UserId playerTwo) {
		return new CreateGame(
				ofNullable(playerOne).orElse(createAnyUserId()),
				ofNullable(playerTwo).orElse(createAnyUserId())
		);
	}

}
