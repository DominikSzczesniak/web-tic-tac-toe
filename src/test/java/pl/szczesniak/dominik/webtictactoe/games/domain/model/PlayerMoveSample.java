package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import pl.szczesniak.dominik.webtictactoe.users.domain.model.UserId;

import java.util.Random;

public class PlayerMoveSample {

	public static GameMoveToMake createAnyPlayerMove(final UserId playerId) {
		final Random random = new Random();
		int row = random.nextInt(3);
		int col = random.nextInt(3);
		return new GameMoveToMake(row, col, playerId);
	}

}
