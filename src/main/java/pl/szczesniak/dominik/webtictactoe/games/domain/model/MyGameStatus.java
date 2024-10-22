package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameStatus;

public enum MyGameStatus {
	IN_PROGRESS,
	DRAW,
	WIN;

	public static MyGameStatus fromGameStatus(final GameStatus gameStatus) {
		return switch (gameStatus) {
			case IN_PROGRESS -> MyGameStatus.IN_PROGRESS;
			case DRAW -> MyGameStatus.DRAW;
			case WIN -> MyGameStatus.WIN;
		};
	}

}
