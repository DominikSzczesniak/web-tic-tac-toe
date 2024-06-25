package pl.szczesniak.dominik.webtictactoe.games.domain.model;

import pl.szczesniak.dominik.tictactoe.core.singlegame.domain.model.GameStatus;
import pl.szczesniak.dominik.webtictactoe.commons.domain.model.exceptions.ObjectDoesNotExistException;

public enum MyGameStatus {
	IN_PROGRESS,
	DRAW,
	WIN;

	public static MyGameStatus fromGameStatus(final GameStatus gameStatus) {
		switch (gameStatus) {
			case IN_PROGRESS:
				return MyGameStatus.IN_PROGRESS;
			case DRAW:
				return MyGameStatus.DRAW;
			case WIN:
				return MyGameStatus.WIN;
			default:
				throw new ObjectDoesNotExistException("Unknown GameStatus: " + gameStatus);
		}
	}

}
