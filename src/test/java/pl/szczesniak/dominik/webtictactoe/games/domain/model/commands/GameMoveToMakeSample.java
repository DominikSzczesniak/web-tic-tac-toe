package pl.szczesniak.dominik.webtictactoe.games.domain.model.commands;

import lombok.Builder;
import pl.szczesniak.dominik.webtictactoe.games.domain.model.GameMoveToMake;
import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Random;

import static java.util.Optional.ofNullable;
import static pl.szczesniak.dominik.webtictactoe.users.domain.model.UserIdSample.createAnyUserId;

public class GameMoveToMakeSample {
	
	@Builder
	private static GameMoveToMake build(final Integer row, final Integer column, final UserId player) {
		final Random random = new Random();
		return new GameMoveToMake(
				ofNullable(row).orElse(random.nextInt(3)),
				ofNullable(column).orElse(random.nextInt(3)),
				ofNullable(player).orElse(createAnyUserId())

		);
	}
	
}
