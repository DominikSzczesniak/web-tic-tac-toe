package pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions;

public class GameDoesNotExistException extends RuntimeException {

	public GameDoesNotExistException(final String message) {
		super(message);
	}
}
