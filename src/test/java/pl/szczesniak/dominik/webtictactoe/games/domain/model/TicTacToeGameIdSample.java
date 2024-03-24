package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import java.util.Random;

public class TicTacToeGameIdSample {

	public static TicTacToeGameId createAnyTicTacToeGameId() {
		return new TicTacToeGameId(new Random().nextLong(1, 99999));
	}

}
