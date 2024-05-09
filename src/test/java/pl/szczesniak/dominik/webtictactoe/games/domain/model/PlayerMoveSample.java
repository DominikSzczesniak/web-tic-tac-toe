package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.PlayerMove;

import java.util.Random;

public class PlayerMoveSample {

	public static PlayerMove createAnyPlayerMove() {
			final Random random = new Random();
			int row = random.nextInt(3);
			int col = random.nextInt(3);
			return new PlayerMove(row, col);
	}

}
