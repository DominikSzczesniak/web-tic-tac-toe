package pl.szczesniak.dominik.webtictactoe.game.domain.model.exceptions;

public class NotEnoughPlayersException extends RuntimeException {

	public NotEnoughPlayersException(final String message) {
		super(message);
	}

}
